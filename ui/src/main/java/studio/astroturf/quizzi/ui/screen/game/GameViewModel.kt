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
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomStateUpdater
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.network.GameConnectionStatus
import studio.astroturf.quizzi.domain.repository.AuthRepository
import studio.astroturf.quizzi.domain.repository.FeedbackRepository
import studio.astroturf.quizzi.domain.repository.GameRepository
import studio.astroturf.quizzi.domain.repository.RoomsRepository
import studio.astroturf.quizzi.ui.base.BaseViewModel
import studio.astroturf.quizzi.ui.extensions.resolve
import studio.astroturf.quizzi.ui.navigation.NavDestination
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import studio.astroturf.quizzi.ui.screen.game.composables.roundend.RoundWinner
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "GameViewModel"

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val authRepository: AuthRepository,
        private val roomsRepository: RoomsRepository,
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
        private var roomId: String? = savedStateHandle[NavDestination.Game.ARG_ROOM_ID]

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

        private lateinit var players: List<Player>

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
            initializeGameRoom()
        }

        private fun observeGameConnectionState() {
            launchIO {
                gameRepository.observeConnectionStatus().collect {
                    clearNotification()
                    when (it) {
                        is GameConnectionStatus.Reconnecting -> {
                            isReconnecting = true
                            _notification.value = UiNotification.Toast("Reconnecting. . . Attempt: ${it.attempt}")
                        }
                        is GameConnectionStatus.Connected -> {
                            val isReconnection = isReconnecting

                            if (isReconnection) {
                                isReconnecting = false
                                rejoinRoom(roomId!!)
                                _notification.value = UiNotification.Toast("Reconnected! Rejoining the room...")
                            }
                        }
                        else -> {
                            isReconnecting = false
                            _notification.value = UiNotification.Toast(message = it::class.simpleName.toString())
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

        private fun initializeGameRoom() {
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
                        roomId?.let { joinRoom(it) } ?: createRoom()
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

        private fun savePlayers(players: List<Player>) {
            this.players = players
        }

        private fun createLobbyState(gameRoomState: GameRoomState.Waiting): GameUiState.Lobby {
            val creator = gameRoomState.players.first()
            return GameUiState.Lobby(
                roomName = "${creator.name}'s Room",
                creator = creator,
                challenger = gameRoomState.players.getOrNull(1),
                countdown = null,
            )
        }

        private fun handleGameEffect(effect: GameRoomStateUpdater) {
            when (effect) {
                is GameRoomStateUpdater.RoomCreated -> {
                    roomId = effect.message.roomId
                    sendPlayerReady()
                }

                is GameRoomStateUpdater.RoomJoined -> {
                    roomId = effect.message.roomId
                    sendPlayerReady()
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
                                player2 = gameState.players[1],
                                gameBarPercentage = INITIAL_GAMEBAR_PERCENTAGE,
                                question = effect.message.currentQuestion,
                                timeRemainingInSeconds = INITIAL_ROUND_COUNTDOWN_SEC,
                                selectedAnswerId = null,
                                playerRoundResult = null,
                            )
                        }

                        Timber.tag(TAG).w("Forced round start from unexpected state: ${currentState::class.simpleName}")
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
                        player2 = gameState.players[1],
                        gameBarPercentage = INITIAL_GAMEBAR_PERCENTAGE,
                        question = effect.message.currentQuestion,
                        timeRemainingInSeconds = INITIAL_ROUND_COUNTDOWN_SEC,
                        selectedAnswerId = null,
                        playerRoundResult = null,
                    )
                }
            }
        }

        private fun handleRoundEnd(effect: GameRoomStateUpdater.RoundEnd) {
            launchMain {
                val currentState = _uiState.value as? GameUiState.RoundOn ?: return@launchMain
                val winner =
                    listOf(currentState.player1, currentState.player2)
                        .find { it.id == effect.message.winnerPlayerId }

                val roundWinner: RoundWinner =
                    when (winner?.id) {
                        null -> RoundWinner.None
                        authRepository.getCurrentPlayerId() -> RoundWinner.Me(winner.id, winner.name)
                        else -> RoundWinner.Opponent(winner.id, winner.name)
                    }

                val correctAnswerValue =
                    currentState.question.options
                        .find { it.id == effect.message.correctAnswer }
                        ?.value ?: return@launchMain

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
                        countdown = GameUiState.Lobby.CountdownTimer(effect.message.remaining.toInt()),
                    )
                }
            }
        }

        private fun handleGameOver(effect: GameRoomStateUpdater.GameRoomOver) {
            launchMain {
                val gameState = currentGameRoomState as? GameRoomState.Playing ?: return@launchMain
                val winner = gameState.players.first { it.id == effect.message.winnerPlayerId }
                updateUiState {
                    GameUiState.GameOver(
                        totalRoundCount = 0, // TODO:
                        winner = winner,
                        gameId = roomId!!,
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

        fun createRoom() {
            gameRepository.sendMessage(
                ClientMessage.CreateRoom(
                    categoryId = 1,
                    gameType = "ResistanceGame",
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
                gameRepository.disconnect()
                roomId = null
            }
        }

        companion object {
            private const val INITIAL_GAMEBAR_PERCENTAGE = 0.5f
            private const val INITIAL_ROUND_COUNTDOWN_SEC = 10
        }
    }
