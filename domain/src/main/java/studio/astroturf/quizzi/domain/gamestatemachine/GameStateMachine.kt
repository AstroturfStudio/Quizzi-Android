package studio.astroturf.quizzi.domain.gamestatemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.model.GameStatistics
import studio.astroturf.quizzi.domain.model.statemachine.Destination
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameIntent
import studio.astroturf.quizzi.domain.model.statemachine.GameState
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

    override fun sideEffect(effect: GameEffect) {
        coroutineScope.launch {
            _effectChannel.send(effect)
        }
    }

    override fun reduce(intent: GameIntent) {
        val currentState = getCurrentState()

        val newState = when (currentState) {
            GameState.Idle -> reduceIdleState(intent)
            is GameState.Lobby -> reduceLobbyState(intent)
            is GameState.Initializing -> reduceInitializingState(intent)
            is GameState.RoundActive -> reduceRoundActiveState(intent)
            is GameState.Paused -> reducePausedState(intent)
            is GameState.RoundEnd -> reduceRoundEndState(intent)
            is GameState.GameOver -> reduceGameOverState(intent)
        }

        _currentStateFlow.value = newState
    }

    private fun reduceIdleState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.RoomCreated -> {
                GameState.Lobby(
                    players = listOf(),
                    isReady = mapOf()
                )
            }

            is GameIntent.RoomJoined -> {
                GameState.Lobby(
                    players = listOf(),
                    isReady = mapOf()
                )
            }

            is GameIntent.Initialize -> {
                GameState.Initializing(intent.message.timeRemaining!!.toInt())
            }

            is GameIntent.CloseRoom -> {
                TODO()
            }

            GameIntent.ExitGame -> {
                TODO()
            }

            is GameIntent.PlayerDisconnected -> {
                TODO()
            }

            is GameIntent.PlayerReconnected -> {
                TODO()
            }

            else -> throw IllegalStateException("$intent couldn't be reduced when current state is Idle}")

        }
    }

    private fun reduceLobbyState(intent: GameIntent): GameState {
        return when (intent) {
            GameIntent.ExitGame -> {
                sideEffect(GameEffect.NavigateTo(Destination.Rooms))
                GameState.Idle
            }

            is GameIntent.CloseRoom -> {
                sideEffect(GameEffect.ShowError(intent.message.reason))
                GameState.Idle
            }

            is GameIntent.Initialize -> {
                GameState.Initializing(intent.message.timeRemaining!!.toInt())
            }

            is GameIntent.PlayerDisconnected -> {
                TODO()
            }

            is GameIntent.PlayerReconnected -> {
                TODO()
            }

            else -> throw IllegalStateException("$intent couldn't be reduced when current state is Lobby}")
        }
    }

    private fun reduceInitializingState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.Playing -> {
                GameState.RoundActive(
                    players = intent.roomUpdate.players,
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    timeRemaining = intent.roomUpdate.timeRemaining,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                )
            }

            is GameIntent.Initialize -> {
                GameState.Initializing(intent.message.timeRemaining!!.toInt())
            }

            else -> throw IllegalStateException("$intent couldn't be reduced when current state is Initializing}")
        }
    }

    private fun reduceRoundActiveState(intent: GameIntent): GameState {
        return when (intent) {
            GameIntent.ExitGame -> {
                sideEffect(GameEffect.NavigateTo(Destination.Rooms))
                getCurrentState()
            }

            is GameIntent.GameOver -> {
                GameState.GameOver(
                    winnerPlayerId = intent.gameOver.winnerPlayerId,
                    statistics = GameStatistics(
                        roundCount = 10,
                        averageResponseTimeMillis = mapOf(),
                        totalGameLengthMillis = 10
                    )
                )
            }

            is GameIntent.Playing -> {
                (getCurrentState() as GameState.RoundActive).copy(
                    players = intent.roomUpdate.players,
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    timeRemaining = intent.roomUpdate.timeRemaining,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                )
            }

            is GameIntent.RoundCompleted -> {
                TODO()
            }

            is GameIntent.RoundTimeUp -> {
                TODO()
            }

            else -> throw IllegalStateException("$intent couldn't be reduced when current state is RoundActive}")
        }
    }

    private fun reducePausedState(intent: GameIntent): GameState {
        return when (intent) {

            is GameIntent.PlayerReconnected -> {
                TODO()
            }

            is GameIntent.Playing -> {
                GameState.RoundActive(
                    players = intent.roomUpdate.players,
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    timeRemaining = intent.roomUpdate.timeRemaining,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                )
            }

            else -> throw IllegalStateException("$intent couldn't be reduced when current state is Paused}")
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
                        totalGameLengthMillis = 10
                    )
                )
            }

            is GameIntent.Playing -> {
                GameState.RoundActive(
                    players = intent.roomUpdate.players,
                    currentQuestion = intent.roomUpdate.currentQuestion!!,
                    timeRemaining = intent.roomUpdate.timeRemaining,
                    cursorPosition = intent.roomUpdate.cursorPosition,
                )
            }

            else -> throw IllegalStateException("$intent couldn't be reduced when current state is RoundEnd}")
        }
    }


    private fun reduceGameOverState(intent: GameIntent): GameState {
        return when (intent) {
            GameIntent.ExitGame -> {
                sideEffect(GameEffect.NavigateTo(Destination.Rooms))
                GameState.Idle
            }

            is GameIntent.CloseRoom -> {
                sideEffect(GameEffect.ShowError(intent.message.reason))
                GameState.Idle
            }

            else -> throw IllegalStateException("$intent couldn't be reduced when current state is GameOver}")
        }
    }
}
