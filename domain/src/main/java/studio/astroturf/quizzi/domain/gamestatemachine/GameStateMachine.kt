package studio.astroturf.quizzi.domain.gamestatemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.model.GameStatistics
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameIntent
import studio.astroturf.quizzi.domain.model.statemachine.GameState
import studio.astroturf.quizzi.domain.model.statemachine.NavDestination
import studio.astroturf.quizzi.domain.model.statemachine.StateMachine

class GameStateMachine(
    val coroutineScope: CoroutineScope
) : StateMachine<GameState, GameIntent, GameEffect> {

    private val _currentStateFlow = MutableStateFlow<GameState>(GameState.Idle)
    val stateFlow = _currentStateFlow.asStateFlow()

    private val _effectChannel = Channel<GameEffect>()
    val effectFlow = _effectChannel.receiveAsFlow()

    override fun getCurrentState(): GameState {
        return _currentStateFlow.value
    }

    override fun effect(effect: GameEffect) {
        coroutineScope.launch {
            _effectChannel.send(effect)
        }
    }

    override fun reduce(intent: GameIntent) {
        val currentState = getCurrentState()

        val newState = when (currentState) {
            GameState.Idle -> reduceIdleState(intent)
            is GameState.GameOver -> reduceGameOverState(intent)
            is GameState.Lobby -> reduceLobbyState(intent)
            is GameState.RoundActive -> reduceRoundActiveState(intent)
            is GameState.RoundEnd -> reduceRoundEndState(intent)
            is GameState.Initializing -> reduceInitializingState(intent)
        }

        _currentStateFlow.value = newState
    }

    private fun reduceInitializingState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.Countdown -> {
                (getCurrentState() as GameState.Initializing).copy(
                    timeRemaining = intent.timeRemaining
                )
            }

            is GameIntent.UpdateRoom -> {
                GameState.RoundActive(
                    players = intent.roomUpdate.players,
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    timeRemaining = intent.roomUpdate.timeRemaining,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                    currentAnswerIndex = null,
                )
            }

            else -> getCurrentState()
        }
    }

    private fun reduceRoundEndState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.GameOver -> {
                GameState.GameOver(
                    winnerPlayerId = intent.gameOver.winnerPlayerId,
                    statistics = GameStatistics(
                        roundCount = 10,
                        averageResponseTimeMillis = mapOf(),
                        totalGameLengthMillis = 1
                    )
                )
            }

            is GameIntent.UpdateRoom -> {
                GameState.RoundActive(
                    players = intent.roomUpdate.players,
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    timeRemaining = intent.roomUpdate.timeRemaining,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                    currentAnswerIndex = null
                )
            }

            else -> getCurrentState()
        }
    }

    private fun reduceRoundActiveState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.SelectAnswer -> {
                (getCurrentState() as GameState.RoundActive).copy(
                    currentAnswerIndex = intent.answerIndex
                )
            }

            is GameIntent.TimeUpdate -> {
                (getCurrentState() as GameState.RoundActive).copy(
                    timeRemaining = intent.update.remaining
                )
            }

            is GameIntent.AnswerResult -> {
                (getCurrentState() as GameState.RoundActive).copy(
                    // TODO: highlight index
                )
            }

            GameIntent.ReturnToRooms -> {
                effect(GameEffect.NavigateTo(NavDestination.Rooms))
                getCurrentState()
            }

            is GameIntent.RoundResult -> TODO()

            is GameIntent.UpdateRoom -> {
                (getCurrentState() as GameState.RoundActive).copy(
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                    timeRemaining = intent.roomUpdate.timeRemaining
                )
            }

            else -> getCurrentState()
        }
    }

    private fun reduceLobbyState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.Countdown -> {
                GameState.Initializing(
                    timeRemaining = 3
                )
            }

            GameIntent.ReturnToRooms -> {
                effect(GameEffect.NavigateTo(NavDestination.Rooms))
                getCurrentState()
            }

            is GameIntent.UpdateRoom -> {
                GameState.RoundActive(
                    players = intent.roomUpdate.players,
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    timeRemaining = intent.roomUpdate.timeRemaining,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                    currentAnswerIndex = null
                )
            }

            else -> getCurrentState()
        }
    }

    private fun reduceIdleState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.CreateLobby -> {
                GameState.Lobby(
                    creator = intent.creator,
                    invitee = null,
                    isReady = mapOf(),
                )
            }

            else -> getCurrentState()
        }
    }

    private fun reduceGameOverState(intent: GameIntent): GameState {
        return when (intent) {
            GameIntent.ReturnToRooms -> {
                effect(GameEffect.NavigateTo(NavDestination.Rooms))
                getCurrentState()
            }

            else -> getCurrentState()
        }
    }
}