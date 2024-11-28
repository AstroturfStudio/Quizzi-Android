package studio.astroturf.quizzi.ui.screen.game

import NavDestination
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import studio.astroturf.quizzi.domain.gameroomstatemachine.GameRoomStateMachine
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomStateUpdater
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.repository.QuizRepository
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "GameViewModel"

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val repository: QuizRepository,
    ) : ViewModel() {
        private val roomId: String? = savedStateHandle[NavDestination.Game.ARG_ROOM_ID]

        private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Idle)
        val uiState = _uiState.asStateFlow()

        private val _uiEvents = Channel<GameUiEvent>(Channel.BUFFERED)
        val uiEvents = _uiEvents.receiveAsFlow()

        private val gameStateMachine = GameRoomStateMachine(viewModelScope, repository)
        private val stateMutex = Mutex()

        val currentGameRoomState: GameRoomState
            get() = gameStateMachine.getCurrentState()

        init {
            observeGameState()
            observeGameEffects()
            initializeGameRoom()
        }

        private fun initializeGameRoom() {
            repository.connect()
            roomId?.let { joinRoom(it) } ?: createRoom()
        }

        private fun observeGameState() {
            viewModelScope.launch {
                gameStateMachine.state.collect { gameRoomState ->
                    updateUiStateSafely(gameRoomState)
                }
            }
        }

        private fun observeGameEffects() {
            viewModelScope.launch {
                gameStateMachine.effects.collect { effect ->
                    handleGameEffectSafely(effect)
                }
            }
        }

        private suspend fun updateUiStateSafely(gameRoomState: GameRoomState) {
            try {
                stateMutex.withLock {
                    updateUiState(gameRoomState)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error updating UI state")
            }
        }

        private fun updateUiState(gameRoomState: GameRoomState) {
            _uiState.value =
                when (gameRoomState) {
                    GameRoomState.Idle -> GameUiState.Idle
                    GameRoomState.Countdown -> _uiState.value // Preserve current state during countdown
                    GameRoomState.Paused -> _uiState.value // Preserve current state while paused
                    is GameRoomState.Playing -> createPlayingState(gameRoomState)
                    is GameRoomState.Waiting -> createLobbyState(gameRoomState)
                    is GameRoomState.Closed -> createGameOverState()
                }
        }

        private suspend fun handleGameEffectSafely(effect: GameRoomStateUpdater) {
            try {
                stateMutex.withLock {
                    handleGameEffect(effect)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error handling game effect")
            }
        }

        private fun createPlayingState(gameRoomState: GameRoomState.Playing): GameUiState.RoundOn =
            GameUiState.RoundOn(
                player1 = gameRoomState.players[0],
                player2 = gameRoomState.players[1],
                gameBarPercentage = INITIAL_GAMEBAR_PERCENTAGE,
                question = null,
                timeRemainingInSeconds = INITIAL_ROUND_COUNTDOWN_SEC,
                selectedAnswerId = null,
                playerRoundResult = null,
            )

        private fun createLobbyState(gameRoomState: GameRoomState.Waiting): GameUiState.Lobby {
            val creator = gameRoomState.players.first()
            return GameUiState.Lobby(
                roomName = "${creator.name}'s Room",
                creator = creator,
                challenger = gameRoomState.players.getOrNull(1),
            )
        }

        private fun createGameOverState(): GameUiState.GameOver = GameUiState.GameOver(totalRoundCount = 0, winner = null)

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
            _uiState.value =
                currentState.copy(
                    playerRoundResult =
                        PlayerRoundResult(
                            answerId = effect.answerResult.answer,
                            isCorrect = effect.answerResult.correct,
                        ),
                )
        }

        private fun handleRoundStart(effect: GameRoomStateUpdater.RoundStarted) {
            when (val currentState = _uiState.value) {
                is GameUiState.RoundOn -> updateExistingRound(currentState, effect)
                is GameUiState.RoundEnd -> createNewRound(currentState, effect)
                else -> Unit
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

            val correctAnswerValue =
                currentState.question
                    ?.options
                    ?.find { it.id == effect.message.correctAnswer }
                    ?.value ?: return

            _uiState.value =
                GameUiState.RoundEnd(
                    roundNo = 0, // TODO: Implement round counting
                    roundWinner = winner,
                    correctAnswerValue = correctAnswerValue,
                    newCursorPosition = effect.message.cursorPosition,
                )
        }

        private fun handleCountdown(effect: GameRoomStateUpdater.Countdown) {
            _uiState.value = GameUiState.Starting(effect.message.remaining.toInt())
        }

        private fun handleGameOver(effect: GameRoomStateUpdater.GameRoomOver) {
            val gameState = currentGameRoomState as? GameRoomState.Closed ?: return
            val winner = gameState.players.find { it.id == effect.message.winnerPlayerId }
            _uiState.value = GameUiState.GameOver(totalRoundCount = 0, winner = winner)
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
            viewModelScope.launch {
                stateMutex.withLock {
                    (_uiState.value as? GameUiState.RoundOn)?.let { currentState ->
                        _uiState.value = currentState.copy(selectedAnswerId = answerId)
                        repository.sendMessage(ClientMessage.PlayerAnswer(answerId))
                    }
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            repository.disconnect()
        }

        companion object {
            const val INITIAL_ROUND_COUNTDOWN_SEC = 20
            const val INITIAL_GAMEBAR_PERCENTAGE = 0.5f
        }
    }
