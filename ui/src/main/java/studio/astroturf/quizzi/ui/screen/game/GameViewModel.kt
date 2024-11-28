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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.gameroomstatemachine.GameRoomStateMachine
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomStateUpdater
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.repository.QuizRepository
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import javax.inject.Inject

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
        val currentUiState: GameUiState
            get() = _uiState.value

        private val _uiEvents = Channel<GameUiEvent>()
        val uiEvents = _uiEvents.receiveAsFlow()

        private val gsm = GameRoomStateMachine(viewModelScope, repository)
        val currentGameRoomState: GameRoomState
            get() = gsm.getCurrentState()

        init {
            viewModelScope.launch {
                gsm.state
                    .collect { gameRoomState ->
                        onGameRoomUpdate(gameRoomState)
                    }
            }

            viewModelScope.launch {
                gsm.effects.collect { effect ->
                    handleGameEffect(effect)
                }
            }

            repository.connect()

            roomId?.let {
                joinRoom(it)
            } ?: createRoom()
        }

        private fun onGameRoomUpdate(gameRoomState: GameRoomState) {
            when (gameRoomState) {
                GameRoomState.Idle -> {
                    _uiState.value = GameUiState.Idle
                }
                GameRoomState.Countdown -> {
                    // TODO
                }
                GameRoomState.Paused -> {
                    // TODO
                }
                is GameRoomState.Playing -> {
                    _uiState.value =
                        GameUiState.RoundOn(
                            player1 = gameRoomState.players.get(0),
                            player2 = gameRoomState.players.get(1),
                            gameBarPercentage = INITIAL_GAMEBAR_PERCENTAGE_FLOAT,
                            question = null,
                            timeRemainingInSeconds = INITIAL_ROUND_COUNTDOWN_SEC,
                            selectedAnswerId = null,
                            playerRoundResult = null,
                        )
                }
                is GameRoomState.Waiting -> {
                    val creator = gameRoomState.players.first()
                    val challenger = gameRoomState.players.getOrNull(1)

                    _uiState.update {
                        GameUiState.Lobby(
                            roomName = "${creator.name}'s Room",
                            creator = creator,
                            challenger = challenger,
                        )
                    }
                }
                is GameRoomState.Closed -> {
                    _uiState.update {
                        GameUiState.GameOver(
                            totalRoundCount = 0,
                            winner = null,
                        )
                    }
                }
            }
        }

        private fun handleGameEffect(effect: GameRoomStateUpdater) {
            when (effect) {
                is GameRoomStateUpdater.PlayerDisconnected -> {
                    // TODO
                }

                is GameRoomStateUpdater.PlayerReconnected -> {
                    // TODO
                }

                is GameRoomStateUpdater.ReceiveAnswerResult -> {
                    val currentState = _uiState.value
                    if (currentState is GameUiState.RoundOn) {
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

                is GameRoomStateUpdater.RoomCreated -> {
                    sendPlayerReady()
                }

                is GameRoomStateUpdater.RoomJoined -> {
                    sendPlayerReady()
                }

                is GameRoomStateUpdater.RoundStarted -> {
                    with(effect.message) {
                        val currentState = _uiState.value

                        _uiState.value =
                            when (currentState) {
                                is GameUiState.RoundOn -> {
                                    currentState.copy(
                                        question = currentQuestion,
                                        timeRemainingInSeconds = timeRemaining.toInt(),
                                        selectedAnswerId = null,
                                        playerRoundResult = null,
                                    )
                                }

                                is GameUiState.RoundEnd -> {
                                    val currentGameRoomState = currentGameRoomState as GameRoomState.Playing
                                    GameUiState.RoundOn(
                                        player1 = currentGameRoomState.players.get(0),
                                        player2 = currentGameRoomState.players.get(1),
                                        gameBarPercentage = currentState.newCursorPosition,
                                        question = currentQuestion,
                                        timeRemainingInSeconds = INITIAL_ROUND_COUNTDOWN_SEC,
                                        selectedAnswerId = null,
                                        playerRoundResult = null,
                                    )
                                }

                                else -> currentState
                            }
                    }
                }

                is GameRoomStateUpdater.RoundTimeUp -> {
                    // TODO
                }

                is GameRoomStateUpdater.RoundEnd -> {
                    val currentState = _uiState.value
                    if (currentState is GameUiState.RoundOn) {
                        val winner: Player? =
                            listOf(currentState.player1, currentState.player2)
                                .find {
                                    it.id == effect.message.winnerPlayerId
                                }

                        val correctAnswerValue =
                            currentState.question!!
                                .options
                                .first { it.id == effect.message.correctAnswer }
                                .value

                        _uiState.value =
                            GameUiState.RoundEnd(
                                roundNo = 0, // TODO: Gelecekkk
                                roundWinner = winner,
                                correctAnswerValue = correctAnswerValue,
                                newCursorPosition = effect.message.cursorPosition,
                            )
                    }
                }

                is GameRoomStateUpdater.CloseRoom -> {
                    // TODO
                }
                is GameRoomStateUpdater.Countdown -> {
                    _uiState.update {
                        GameUiState.Starting(effect.message.remaining.toInt())
                    }
                }
                GameRoomStateUpdater.ExitGameRoom -> {
                    // TODO
                }
                is GameRoomStateUpdater.GameRoomOver -> {
                    val currentState = _uiState.value
                    if (currentState is GameUiState.GameOver) {
                        val winner = (currentGameRoomState as GameRoomState.Closed).players.first { it.id == effect.message.winnerPlayerId }
                        _uiState.update {
                            currentState.copy(totalRoundCount = 0, winner = winner)
                        }
                    }
                }
                is GameRoomStateUpdater.RoundTimeUpdate -> {
                    val currentState = _uiState.value
                    if (currentState is GameUiState.RoundOn) {
                        _uiState.value =
                            currentState.copy(
                                timeRemainingInSeconds = effect.message.remaining.toInt(),
                            )
                    }
                }

                is GameRoomStateUpdater.Error -> {
                    // TODO
                }
            }
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
                // Update UI state to show selected answer
                val currentState = _uiState.value
                if (currentState is GameUiState.RoundOn) {
                    _uiState.value =
                        currentState.copy(
                            selectedAnswerId = answerId,
                        )
                }

                // Send answer to server
                repository.sendMessage(ClientMessage.PlayerAnswer(answerId))
            }
        }

        override fun onCleared() {
            super.onCleared()
            repository.disconnect()
        }

        companion object {
            const val INITIAL_ROUND_COUNTDOWN_SEC = 20
            const val INITIAL_GAMEBAR_PERCENTAGE_FLOAT = 0.5f
        }
    }
