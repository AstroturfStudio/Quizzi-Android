package studio.astroturf.quizzi.ui.screen.game

import NavDestination
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import studio.astroturf.quizzi.domain.di.DefaultDispatcher
import studio.astroturf.quizzi.domain.di.IoDispatcher
import studio.astroturf.quizzi.domain.gameroomstatemachine.GameRoomStateMachine
import studio.astroturf.quizzi.domain.model.GameFeedback
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomStateUpdater
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.repository.FeedbackRepository
import studio.astroturf.quizzi.domain.repository.QuizRepository
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
        private val repository: QuizRepository,
        private val feedbackRepository: FeedbackRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
        val imageLoader: ImageLoader,
    ) : ViewModel() {
        private val roomId: String? = savedStateHandle[NavDestination.Game.ARG_ROOM_ID]

        private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Idle)
        val uiState =
            _uiState
                .buffer(Channel.UNLIMITED)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = GameUiState.Idle,
                )

        private val _uiEvents =
            MutableSharedFlow<GameUiEvent>(
                extraBufferCapacity = 64,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )
        val uiEvents = _uiEvents.asSharedFlow()

        private val gameStateMachine = GameRoomStateMachine(viewModelScope, repository, defaultDispatcher)
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

        init {
            observeGameState()
            observeGameEffects()
            processStateUpdatesSequentially()
            initializeGameRoom()
        }

        private fun processStateUpdatesSequentially() {
            viewModelScope.launch {
                for (update in stateUpdateChannel) {
                    stateMutex.withLock {
                        when (update) {
                            is StateUpdate.FromGameState -> {
                                val newUiState = processGameState(update.gameState)
                                withContext(Dispatchers.Main) {
                                    _uiState.value = newUiState
                                }
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
            repository.connect()
            roomId?.let { joinRoom(it) } ?: createRoom()
        }

        private fun observeGameState() {
            viewModelScope.launch {
                gameStateMachine.state.collect { gameRoomState ->
                    stateUpdateChannel.send(StateUpdate.FromGameState(gameRoomState))
                }
            }
        }

        private fun observeGameEffects() {
            viewModelScope.launch {
                gameStateMachine.effects.collect { effect ->
                    stateUpdateChannel.send(StateUpdate.FromEffect(effect))
                }
            }
        }

        private suspend fun processGameState(gameRoomState: GameRoomState): GameUiState =
            when (gameRoomState) {
                GameRoomState.Idle -> GameUiState.Idle
                GameRoomState.Countdown -> _uiState.value // Preserve current state
                GameRoomState.Paused -> _uiState.value // Preserve current state
                is GameRoomState.Playing -> {
                    savePlayers(gameRoomState.players)
                    _uiState.value
                }
                is GameRoomState.Waiting -> createLobbyState(gameRoomState)
                is GameRoomState.Closed -> createGameOverState()
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

        private fun createGameOverState(): GameUiState.GameOver =
            GameUiState.GameOver(
                totalRoundCount = 0,
                winner = null,
                gameId = roomId ?: "",
            )

        private fun handleGameEffect(effect: GameRoomStateUpdater) {
            when (effect) {
                is GameRoomStateUpdater.RoomCreated,
                is GameRoomStateUpdater.RoomJoined,
                -> sendPlayerReady()

                is GameRoomStateUpdater.ReceiveAnswerResult -> handleAnswerResult(effect)
                is GameRoomStateUpdater.RoundStarted -> handleRoundStart(effect)
                is GameRoomStateUpdater.RoundEnd -> handleRoundEnd(effect)
                is GameRoomStateUpdater.Countdown -> handleCountdown(effect)
                is GameRoomStateUpdater.GameRoomOver -> handleGameOver(effect)
                is GameRoomStateUpdater.RoundTimeUpdate -> handleTimeUpdate(effect)

                // TODO: Implement remaining effect handlers
                is GameRoomStateUpdater.PlayerDisconnected,
                is GameRoomStateUpdater.PlayerReconnected,
                is GameRoomStateUpdater.RoundTimeUp,
                is GameRoomStateUpdater.CloseRoom,
                GameRoomStateUpdater.ExitGameRoom,
                is GameRoomStateUpdater.Error,
                -> Unit
            }
        }

        private fun handleAnswerResult(effect: GameRoomStateUpdater.ReceiveAnswerResult) {
            val currentState = _uiState.value as? GameUiState.RoundOn ?: return
            val isCurrentPlayerResult: Boolean = effect.answerResult.playerId == repository.getCurrentPlayerId()
            if (isCurrentPlayerResult) {
                _uiState.value =
                    currentState.copy(
                        playerRoundResult =
                            PlayerRoundResult(
                                answerId = effect.answerResult.answer,
                                isCorrect = effect.answerResult.correct,
                            ),
                    )
            }
        }

        private fun handleRoundStart(effect: GameRoomStateUpdater.RoundStarted) {
            when (val currentState = _uiState.value) {
                is GameUiState.Lobby -> createInitialRound(currentState, effect)
                is GameUiState.RoundOn -> updateExistingRound(currentState, effect)
                is GameUiState.RoundEnd -> createNewRound(currentState, effect)
                else -> {
                    // Force create new round even if in unexpected state
                    val gameState = currentGameRoomState as? GameRoomState.Playing ?: return
                    _uiState.value =
                        GameUiState.RoundOn(
                            player1 = gameState.players[0],
                            player2 = gameState.players[1],
                            gameBarPercentage = INITIAL_GAMEBAR_PERCENTAGE,
                            question = effect.message.currentQuestion,
                            timeRemainingInSeconds = INITIAL_ROUND_COUNTDOWN_SEC,
                            selectedAnswerId = null,
                            playerRoundResult = null,
                        )
                    Timber.tag(TAG).w("Forced round start from unexpected state: ${currentState::class.simpleName}")
                }
            }
        }

        private fun updateExistingRound(
            currentState: GameUiState.RoundOn,
            effect: GameRoomStateUpdater.RoundStarted,
        ) {
            _uiState.value =
                currentState.copy(
                    question = effect.message.currentQuestion,
                    timeRemainingInSeconds = effect.message.timeRemaining.toInt(),
                    selectedAnswerId = null,
                    playerRoundResult = null,
                )
        }

        private fun createInitialRound(
            currentState: GameUiState.Lobby,
            effect: GameRoomStateUpdater.RoundStarted,
        ) {
            val gameState = currentGameRoomState as? GameRoomState.Playing ?: return
            _uiState.value =
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

        private fun createNewRound(
            currentState: GameUiState.RoundEnd,
            effect: GameRoomStateUpdater.RoundStarted,
        ) {
            val gameState = currentGameRoomState as? GameRoomState.Playing ?: return
            _uiState.value =
                GameUiState.RoundOn(
                    player1 = gameState.players[0],
                    player2 = gameState.players[1],
                    gameBarPercentage = currentState.newCursorPosition,
                    question = effect.message.currentQuestion,
                    timeRemainingInSeconds = INITIAL_ROUND_COUNTDOWN_SEC,
                    selectedAnswerId = null,
                    playerRoundResult = null,
                )
        }

        private fun handleRoundEnd(effect: GameRoomStateUpdater.RoundEnd) {
            val currentState = _uiState.value as? GameUiState.RoundOn ?: return
            val winner =
                listOf(currentState.player1, currentState.player2)
                    .find { it.id == effect.message.winnerPlayerId }

            val roundWinner: RoundWinner =
                when (winner?.id) {
                    null -> RoundWinner.None
                    repository.getCurrentPlayerId() -> RoundWinner.Me(winner.id, winner.name)
                    else -> RoundWinner.Opponent(winner.id, winner.name)
                }

            val correctAnswerValue =
                currentState.question
                    .options
                    .find { it.id == effect.message.correctAnswer }
                    ?.value ?: return

            _uiState.value =
                GameUiState.RoundEnd(
                    roundNo = 0, // TODO: Implement round counting
                    roundWinner = roundWinner,
                    correctAnswerValue = correctAnswerValue,
                    newCursorPosition = effect.message.cursorPosition,
                )
        }

        private fun handleCountdown(effect: GameRoomStateUpdater.Countdown) {
            val currentUiState = uiState.value as? GameUiState.Lobby ?: return
            _uiState.value =
                currentUiState.copy(
                    countdown = GameUiState.Lobby.CountdownTimer(effect.message.remaining.toInt()),
                )
        }

        private fun handleGameOver(effect: GameRoomStateUpdater.GameRoomOver) {
            val gameState = currentGameRoomState as? GameRoomState.Closed ?: return
            val winner = gameState.players.find { it.id == effect.message.winnerPlayerId }
            _uiState.value =
                GameUiState.GameOver(
                    totalRoundCount = 0,
                    winner = winner,
                    gameId = roomId ?: "",
                )
        }

        private fun handleTimeUpdate(effect: GameRoomStateUpdater.RoundTimeUpdate) {
            val currentState = _uiState.value as? GameUiState.RoundOn ?: return
            _uiState.value =
                currentState.copy(
                    timeRemainingInSeconds = effect.message.remaining.toInt(),
                )
        }

        fun createRoom() {
            repository.sendMessage(ClientMessage.CreateRoom)
        }

        fun joinRoom(roomId: String) {
            repository.sendMessage(ClientMessage.JoinRoom(roomId))
        }

        fun sendPlayerReady() {
            repository.sendMessage(ClientMessage.PlayerReady)
        }

        fun submitAnswer(answerId: Int) {
            viewModelScope.launch(ioDispatcher) {
                stateMutex.withLock {
                    (_uiState.value as? GameUiState.RoundOn)?.let { currentState ->
                        _uiState.value = currentState.copy(selectedAnswerId = answerId)
                        repository.sendMessage(ClientMessage.PlayerAnswer(answerId))
                    }
                }
            }
        }

        fun submitFeedback(feedback: GameFeedback) {
            viewModelScope.launch(ioDispatcher) {
                try {
                    feedbackRepository.submitFeedback(feedback)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to submit feedback")
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            repository.disconnect()
        }

        companion object {
            const val INITIAL_ROUND_COUNTDOWN_SEC = 10
            const val INITIAL_GAMEBAR_PERCENTAGE = 0.5f
        }
    }
