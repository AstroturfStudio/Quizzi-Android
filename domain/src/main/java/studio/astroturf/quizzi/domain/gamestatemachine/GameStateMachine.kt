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
            is GameState.Starting -> reduceStartingState(intent)
            is GameState.RoundOn -> reduceRoundOnState(intent)
            is GameState.Paused -> reducePausedState(intent)
            is GameState.EndOfRound -> reduceEndOfRoundState(intent)
            is GameState.GameOver -> reduceGameOverState(intent)
        }

        _currentStateFlow.value = newState
    }

    private fun reduceIdleState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.Lobby -> {
                // TODO: Idle'da WAITING gelirse roomId lazım mı
                GameState.Lobby(
                    roomId = intent.message.toString()
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is Idle")
        }
    }

    private fun reduceLobbyState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.Countdown -> {
                GameState.Starting(intent.message.remaining.toInt())
            }

            is GameIntent.Lobby -> {
                getCurrentState()
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is Lobby")
        }
    }

    private fun reduceStartingState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.StartRound -> {
                with(intent.message) {
                    GameState.RoundOn(
                        players = players,
                        currentQuestion = currentQuestion!!,
                        timeRemaining = timeRemaining,
                        cursorPosition = cursorPosition,
                    )
                }
            }

            is GameIntent.Countdown -> {
                GameState.Starting(intent.message.remaining.toInt())
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is Starting")
        }
    }

    private fun reduceRoundOnState(intent: GameIntent): GameState {
        return when (intent) {
            GameIntent.ExitGame -> {
                sideEffect(GameEffect.NavigateTo(Destination.Rooms))
                getCurrentState()
            }

            is GameIntent.GameOver -> {
                GameState.GameOver(
                    winnerPlayerId = intent.message.winnerPlayerId,
                    statistics = GameStatistics(
                        roundCount = 10,
                        averageResponseTimeMillis = mapOf(),
                        totalGameLengthMillis = 10
                    )
                )
            }

            is GameIntent.RoundEnd -> {
                GameState.EndOfRound(
                    cursorPosition = intent.message.cursorPosition,
                    correctAnswer = intent.message.correctAnswer,
                    winnerPlayerId = intent.message.winnerPlayerId
                )
            }

            is GameIntent.RoundTimeUp -> {
                val currentState = getCurrentState() as GameState.RoundOn

                GameState.EndOfRound(
                    cursorPosition = currentState.cursorPosition,
                    correctAnswer = intent.message.correctAnswer,
                    winnerPlayerId = null
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is RoundOn")
        }
    }

    private fun reducePausedState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.StartRound -> {
                GameState.RoundOn(
                    players = intent.message.players,
                    currentQuestion = intent.message.currentQuestion!!,
                    timeRemaining = intent.message.timeRemaining,
                    cursorPosition = intent.message.cursorPosition,
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is Paused")
        }
    }

    private fun reduceEndOfRoundState(intent: GameIntent): GameState {
        return when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.GameOver -> {
                GameState.GameOver(
                    winnerPlayerId = intent.message.winnerPlayerId,
                    statistics = GameStatistics(
                        roundCount = 10,
                        averageResponseTimeMillis = mapOf(),
                        totalGameLengthMillis = 10
                    )
                )
            }

            is GameIntent.StartRound -> {
                GameState.RoundOn(
                    players = intent.message.players,
                    currentQuestion = intent.message.currentQuestion!!,
                    timeRemaining = intent.message.timeRemaining,
                    cursorPosition = intent.message.cursorPosition,
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is EndOfRound")
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

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is GameOver")
        }
    }
}
