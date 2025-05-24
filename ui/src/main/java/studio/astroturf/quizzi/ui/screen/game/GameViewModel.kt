package studio.astroturf.quizzi.ui.screen.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import studio.astroturf.quizzi.domain.di.DefaultDispatcher
import studio.astroturf.quizzi.domain.di.IoDispatcher
import studio.astroturf.quizzi.domain.di.MainDispatcher
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResolver
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.gameroomstatemachine.GameRoomStateMachine
import studio.astroturf.quizzi.domain.model.GameFeedback
import studio.astroturf.quizzi.domain.model.PlayerInRoom
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomStateUpdater
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.network.GameConnectionStatus
import studio.astroturf.quizzi.domain.repository.AuthRepository
import studio.astroturf.quizzi.domain.repository.FeedbackRepository
import studio.astroturf.quizzi.domain.repository.GameRepository
import studio.astroturf.quizzi.ui.base.BaseViewModel
import studio.astroturf.quizzi.ui.extensions.resolve
import studio.astroturf.quizzi.ui.navigation.QuizziNavDestination
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import studio.astroturf.quizzi.ui.screen.game.composables.lobby.LobbyPlayerUiModel
import studio.astroturf.quizzi.ui.screen.game.composables.lobby.LobbyUiModel
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "GameViewModel"

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val authRepository: AuthRepository,
        private val gameRepository: GameRepository,
        private val feedbackRepository: FeedbackRepository,
        private val exceptionResolver: ExceptionResolver,
        @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
        val imageLoader: ImageLoader,
    ) : BaseViewModel(
            mainDispatcher,
            ioDispatcher,
            defaultDispatcher,
        ) {
        private var roomId: String? = savedStateHandle[QuizziNavDestination.Game.ARG_ROOM_ID]
        private var roomName: String? = savedStateHandle[QuizziNavDestination.Game.ARG_ROOM_NAME]
        private var categoryId: String? = savedStateHandle[QuizziNavDestination.Game.ARG_CATEGORY_ID]
        private var gameType: String? = savedStateHandle[QuizziNavDestination.Game.ARG_GAME_TYPE]

        private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Idle)
        val uiState: StateFlow<GameUiState> =
            _uiState
                .buffer(Channel.UNLIMITED)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = GameUiState.Idle,
                )

        private val gameStateMachine =
            GameRoomStateMachine(viewModelScope, gameRepository, defaultDispatcher)
        private val stateMutex = Mutex()

        private lateinit var players: List<PlayerInRoom>

        val currentGameRoomState: GameRoomState
            get() = gameStateMachine.getCurrentState()

        private sealed class StateUpdate {
            data class FromGameState(
                val gameState: GameRoomState,
            ) : StateUpdate()

            data class FromEffect(
                val effect: GameRoomStateUpdater,
            ) : StateUpdate()
        }

        private val stateUpdateChannel = Channel<StateUpdate>(Channel.UNLIMITED)

        private val _notification = MutableStateFlow<UiNotification?>(null)
        val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

        var isReconnecting = false

        init {
            observeGameState()
            observeGameEffects()
            observeGameConnectionState()
            processStateUpdatesSequentially()
            initializeGameRoom(
                roomName = roomName,
                categoryId = categoryId?.toInt(),
                gameType = gameType,
            )
        }

        private fun observeGameConnectionState() {
            launchIO {
                gameRepository.observeConnectionStatus().collect {
                    clearNotification()
                    when (it) {
                        is GameConnectionStatus.Idle -> {
                            isReconnecting = false
                        }

                        is GameConnectionStatus.Reconnecting -> {
                            isReconnecting = true
                            _notification.value =
                                UiNotification.Toast("Reconnecting. . . Attempt: ${it.attempt}")
                        }

                        is GameConnectionStatus.Connected -> {
                            val isReconnection = isReconnecting

                            if (isReconnection) {
                                isReconnecting = false
                                rejoinRoom(roomId!!)
                                _notification.value =
                                    UiNotification.Toast("Reconnected! Rejoining the room...")
                            }
                        }

                        else -> {
                            isReconnecting = false
                            _notification.value =
                                UiNotification.Toast(message = it::class.simpleName.toString())
                        }
                    }
                }
            }
        }

        private fun processStateUpdatesSequentially() {
            launchMain {
                for (update in stateUpdateChannel) {
                    stateMutex.withLock {
                        when (update) {
                            is StateUpdate.FromGameState -> {
                                val newUiState = processGameState(update.gameState)
                                updateUiState { newUiState }
                            }

                            is StateUpdate.FromEffect -> {
                                handleGameEffect(update.effect)
                            }
                        }
                    }
                }
            }
        }

        private fun initializeGameRoom(
            roomName: String?,
            categoryId: Int?,
            gameType: String?,
        ) {
            launchIO {
                gameRepository
                    .connect(authRepository.getCurrentPlayerId())
                    .resolve(
                        exceptionResolver,
                        onUiNotification = {
                            _notification.value = it
                        },
                    ) {
                        // Websocket connected successfully
                        roomId?.let { joinRoom(it) } ?: createRoom(
                            roomName =
                                roomName
                                    ?: throw IllegalStateException("Room name is null when creating room"),
                            categoryId =
                                categoryId
                                    ?: throw IllegalStateException("Category ID is null when creating room"),
                            gameType =
                                gameType
                                    ?: throw IllegalStateException("Game type is null when creating room"),
                        )
                    }
            }
        }

        private fun observeGameState() {
            launchMain {
                gameStateMachine.state.collect { gameRoomState ->
                    stateUpdateChannel.send(StateUpdate.FromGameState(gameRoomState))
                }
            }
        }

        private fun observeGameEffects() {
            launchMain {
                gameStateMachine.effects.collect { effect ->
                    stateUpdateChannel.send(StateUpdate.FromEffect(effect))
                }
            }
        }

        private fun processGameState(gameRoomState: GameRoomState): GameUiState =
            when (gameRoomState) {
                GameRoomState.Idle -> GameUiState.Idle
                is GameRoomState.Playing -> {
                    savePlayers(gameRoomState.players)
                    _uiState.value
                }

                is GameRoomState.Waiting -> createLobbyState(gameRoomState)
                else -> _uiState.value // Preserve current state
            }

        private fun savePlayers(players: List<PlayerInRoom>) {
            this.players = players
        }

        private fun createLobbyState(gameRoomState: GameRoomState.Waiting): GameUiState.Lobby {
            val currentPlayer = gameRoomState.players.first { it.id == authRepository.getCurrentPlayerId() }
            val category = gameRoomState.category
            val gameType = gameRoomState.gameType

            return GameUiState.Lobby(
                lobbyUiModel =
                    LobbyUiModel(
                        roomTitle = roomName ?: "",
                        categoryName = category.name,
                        gameType = gameType,
                        players =
                            gameRoomState.players.mapIndexed { index, it ->
                                LobbyPlayerUiModel(
                                    player = it,
                                    isCreator = index == 0,
                                    isReady = it.isReady,
                                )
                            },
                        currentUserReady = currentPlayer.isReady,
                        countdown = null,
                    ),
            )
        }

        private fun handleGameEffect(effect: GameRoomStateUpdater) {
            when (effect) {
                is GameRoomStateUpdater.RoomCreated -> {
                    roomId = effect.message.roomId
                }

                is GameRoomStateUpdater.RoomJoined -> {
                    roomId = effect.message.roomId
                }

                is GameRoomStateUpdater.RoomRejoined -> {
                    roomId = effect.message.roomId
                }

                is GameRoomStateUpdater.CloseRoom -> {
                    roomId = null
                }

                is GameRoomStateUpdater.ReceiveAnswerResult -> handleAnswerResult(effect)
                is GameRoomStateUpdater.RoundStarted -> handleRoundStart(effect)
                is GameRoomStateUpdater.RoundEnd -> {
                    handleRoundEnd(effect)
                }

                is GameRoomStateUpdater.Countdown -> handleCountdown(effect)
                is GameRoomStateUpdater.GameRoomOver -> handleGameOver(effect)
                is GameRoomStateUpdater.RoundTimeUpdate -> handleTimeUpdate(effect)

                // TODO: Implement remaining effect handlers
                is GameRoomStateUpdater.PlayerDisconnected,
                is GameRoomStateUpdater.PlayerReconnected,
                is GameRoomStateUpdater.RoundTimeUp,
                GameRoomStateUpdater.ExitGameRoom,
                is GameRoomStateUpdater.Error,
                -> Unit
            }
        }

        private fun handleAnswerResult(effect: GameRoomStateUpdater.ReceiveAnswerResult) {
            launchMain {
                val currentState = _uiState.value as? GameUiState.RoundOn ?: return@launchMain
                val isCurrentPlayerResult =
                    effect.answerResult.playerId == authRepository.getCurrentPlayerId()
                if (isCurrentPlayerResult) {
                    updateUiState {
                        currentState.copy(
                            playerRoundResult =
                                PlayerRoundResult(
                                    answerId = effect.answerResult.answer,
                                    isCorrect = effect.answerResult.correct,
                                ),
                        )
                    }
                }
            }
        }

        private fun handleRoundStart(effect: GameRoomStateUpdater.RoundStarted) {
            launchMain {
                when (val currentState = _uiState.value) {
                    is GameUiState.Lobby -> createInitialRound(currentState, effect)
                    is GameUiState.RoundOn -> updateExistingRound(currentState, effect)
                    else -> {
                        val gameState =
                            currentGameRoomState as? GameRoomState.Playing ?: return@launchMain
                        updateUiState {
                            GameUiState.RoundOn(
                                player1 = gameState.players[0],
                                player2 = gameState.players.getOrNull(1),
                                gameBarPercentage = INITIAL_GAMEBAR_PERCENTAGE,
                                question = effect.message.currentQuestion,
                                timeRemainingInSeconds = getInitialRoundCountdownSec(),
                                selectedAnswerId = null,
                                playerRoundResult = null,
                            )
                        }

                        Timber
                            .tag(TAG)
                            .w("Forced round start from unexpected state: ${currentState::class.simpleName}")
                    }
                }
            }
        }

        private fun updateExistingRound(
            currentState: GameUiState.RoundOn,
            effect: GameRoomStateUpdater.RoundStarted,
        ) {
            updateUiState {
                currentState.copy(
                    question = effect.message.currentQuestion,
                    timeRemainingInSeconds = effect.message.timeRemaining.toInt(),
                    selectedAnswerId = null,
                    playerRoundResult = null,
                )
            }
        }

        private fun createInitialRound(
            currentState: GameUiState.Lobby,
            effect: GameRoomStateUpdater.RoundStarted,
        ) {
            launchMain {
                val gameState = currentGameRoomState as? GameRoomState.Playing ?: return@launchMain
                updateUiState {
                    GameUiState.RoundOn(
                        player1 = gameState.players[0],
                        player2 = gameState.players.getOrNull(1),
                        gameBarPercentage = INITIAL_GAMEBAR_PERCENTAGE,
                        question = effect.message.currentQuestion,
                        timeRemainingInSeconds = getInitialRoundCountdownSec(),
                        selectedAnswerId = null,
                        playerRoundResult = null,
                    )
                }
            }
        }

        private fun getInitialRoundCountdownSec(): Int =
            when (gameType) {
                "ResistanceGame" -> 10
                "ResistToTimeGame" -> 3
                else -> 10
            }

        private fun handleRoundEnd(effect: GameRoomStateUpdater.RoundEnd) {
            launchMain {
                val currentState = _uiState.value as? GameUiState.RoundOn ?: return@launchMain

                updateUiState {
                    currentState.copy(
                        gameBarPercentage = effect.message.cursorPosition,
                    )
                }
            }
        }

        private fun handleCountdown(effect: GameRoomStateUpdater.Countdown) {
            launchMain {
                val currentUiState = uiState.value as? GameUiState.Lobby ?: return@launchMain
                updateUiState {
                    currentUiState.copy(
                        lobbyUiModel = currentUiState.lobbyUiModel.copy(countdown = effect.message.remaining.toInt()),
                    )
                }
            }
        }

        private fun handleGameOver(effect: GameRoomStateUpdater.GameRoomOver) {
            launchMain {
                val gameState = currentGameRoomState as? GameRoomState.Playing ?: return@launchMain
                val winner = gameState.players.firstOrNull { it.id == effect.message.winnerPlayerId }
                updateUiState {
                    val gameId: String = roomId!!
                    disconnectFromRoom()
                    GameUiState.GameOver(
                        totalRoundCount = 0, // TODO:
                        winnerName = winner?.name ?: "Quizzi Bot",
                        gameId = gameId,
                    )
                }
            }
        }

        private fun handleTimeUpdate(effect: GameRoomStateUpdater.RoundTimeUpdate) {
            launchMain {
                val currentState = _uiState.value as? GameUiState.RoundOn ?: return@launchMain

                updateUiState {
                    currentState.copy(
                        timeRemainingInSeconds = effect.message.remaining.toInt(),
                    )
                }
            }
        }

        fun createRoom(
            roomName: String,
            categoryId: Int,
            gameType: String,
        ) {
            gameRepository.sendMessage(
                ClientMessage.CreateRoom(
                    roomName = roomName,
                    categoryId = categoryId,
                    gameType = gameType,
                ),
            )
        }

        fun joinRoom(roomId: String) {
            gameRepository.sendMessage(ClientMessage.JoinRoom(roomId))
        }

        fun rejoinRoom(roomId: String) {
            gameRepository.sendMessage(ClientMessage.RejoinRoom(roomId))
        }

        fun sendPlayerReady() {
            gameRepository.sendMessage(ClientMessage.PlayerReady)
        }

        fun submitAnswer(answerId: Int) {
            launchIO {
                stateMutex.withLock {
                    (_uiState.value as? GameUiState.RoundOn)?.let { currentState ->
                        updateUiState {
                            currentState.copy(selectedAnswerId = answerId)
                        }
                        gameRepository.sendMessage(ClientMessage.PlayerAnswer(answerId))
                    }
                }
            }
        }

        fun submitFeedback(feedback: GameFeedback) {
            launchIO {
                feedbackRepository
                    .submitFeedback(feedback)
                    .resolve(
                        exceptionResolver,
                        onUiNotification = {
                            _notification.value = it
                        },
                    ) {
                        // Feedback submitted successfully
                    }
            }
        }

        private fun updateUiState(newState: (GameUiState) -> GameUiState) {
            launchMain {
                _uiState.value = newState(_uiState.value)
            }
        }

        fun clearNotification() {
            launchMain {
                _notification.value = null
            }
        }

        override fun onCleared() {
            super.onCleared()
            launchIO {
                disconnectFromRoom()
            }
        }

        private fun disconnectFromRoom() {
            gameRepository.disconnect()
            roomId = null
        }

        fun readyToPlay() {
            sendPlayerReady()
        }

        fun getGameType(): String? = gameType

        companion object {
            private const val INITIAL_GAMEBAR_PERCENTAGE = 0.5f
        }
    }
