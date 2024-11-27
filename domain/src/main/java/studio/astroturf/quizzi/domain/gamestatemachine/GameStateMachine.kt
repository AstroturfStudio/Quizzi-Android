package studio.astroturf.quizzi.domain.gamestatemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.model.GameStatistics
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.domain.model.statemachine.Destination
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameIntent
import studio.astroturf.quizzi.domain.model.statemachine.GameState
import studio.astroturf.quizzi.domain.model.statemachine.StateMachine
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.AnswerResult
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.Countdown
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.Error
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.GameOver
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.PlayerDisconnected
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.PlayerReconnected
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomClosed
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomCreated
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomJoined
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomUpdate
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoundEnded
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoundUpdate
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.TimeUp
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.TimeUpdate
import timber.log.Timber

class GameStateMachine(
    val coroutineScope: CoroutineScope,
) : StateMachine<GameState, GameIntent, GameEffect> {
    private val _currentStateFlow = MutableStateFlow<GameState>(GameState.Idle)
    val stateFlow = _currentStateFlow.asStateFlow()

    private val _effectChannel = Channel<GameEffect>()
    val effectFlow = _effectChannel.receiveAsFlow()

    override fun getCurrentState(): GameState = _currentStateFlow.value

    override fun sideEffect(effect: GameEffect) {
        coroutineScope.launch {
            _effectChannel.send(effect)
        }
    }

    override fun reduce(intent: GameIntent) {
        val currentState = getCurrentState()

        val newState =
            when (currentState) {
                GameState.Idle -> reduceIdleState(currentState, intent)
                is GameState.Lobby -> reduceLobbyState(currentState, intent)
                is GameState.Starting -> reduceStartingState(currentState, intent)
                is GameState.RoundOn -> reduceRoundOnState(currentState, intent)
                is GameState.Paused -> reducePausedState(currentState, intent)
                is GameState.EndOfRound -> reduceEndOfRoundState(currentState, intent)
                is GameState.GameOver -> reduceGameOverState(currentState, intent)
            }

        _currentStateFlow.value = newState
    }

    private fun reduceIdleState(
        currentState: GameState,
        intent: GameIntent,
    ): GameState =
        when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.Lobby -> {
                // TODO: Idle'da WAITING gelirse roomId lazım mı
                GameState.Lobby(
                    roomId = intent.message.toString(),
                    players = intent.message.players,
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is Idle")
        }

    private fun reduceLobbyState(
        currentState: GameState.Lobby,
        intent: GameIntent,
    ): GameState =
        when (intent) {
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

    private fun reduceStartingState(
        currentState: GameState.Starting,
        intent: GameIntent,
    ): GameState =
        when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.StartRound -> {
                with(intent.message) {
                    GameState.RoundOn(
                        players = players,
                        currentQuestion = currentQuestion!!,
                        timeRemaining = timeRemaining!!,
                        cursorPosition = cursorPosition,
                    )
                }
            }

            is GameIntent.Countdown -> {
                GameState.Starting(intent.message.remaining.toInt())
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is Starting")
        }

    private fun reduceRoundOnState(
        currentState: GameState.RoundOn,
        intent: GameIntent,
    ): GameState =
        when (intent) {
            GameIntent.ExitGame -> {
                sideEffect(GameEffect.NavigateTo(Destination.Rooms))
                getCurrentState()
            }

            is GameIntent.GameOver -> {
                GameState.GameOver(
                    winner = currentState.players.first { it.id == intent.message.winnerPlayerId },
                    statistics =
                        GameStatistics(
                            roundCount = 10,
                            averageResponseTimeMillis = mapOf(),
                            totalGameLengthMillis = 10,
                        ),
                )
            }

            is GameIntent.RoundEnd -> {
                val winner: Player? =
                    currentState.players.find { it.id == intent.message.winnerPlayerId }
                val correctAnswer =
                    currentState.currentQuestion.options[intent.message.correctAnswer]

                GameState.EndOfRound(
                    cursorPosition = intent.message.cursorPosition,
                    correctAnswer = correctAnswer,
                    winnerPlayer = winner,
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is RoundOn")
        }

    private fun reducePausedState(
        currentState: GameState.Paused,
        intent: GameIntent,
    ): GameState =
        when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.StartRound -> {
                GameState.RoundOn(
                    players = intent.message.players,
                    currentQuestion = intent.message.currentQuestion!!,
                    timeRemaining = intent.message.timeRemaining!!,
                    cursorPosition = intent.message.cursorPosition,
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is Paused")
        }

    private fun reduceEndOfRoundState(
        currentState: GameState.EndOfRound,
        intent: GameIntent,
    ): GameState =
        when (intent) {
            is GameIntent.CloseRoom -> TODO()
            GameIntent.ExitGame -> TODO()

            is GameIntent.GameOver -> {
                val winnerPlayerId = intent.message.winnerPlayerId
                GameState.GameOver(
                    winner =
                        Player(
                            // FIXME: currentState.players.first { it.id == intent.message.winnerPlayerId } olmasi lazim
                            id = winnerPlayerId,
                            name = intent.message.winnerPlayerId,
                            avatarUrl = "",
                        ),
                    statistics =
                        GameStatistics(
                            roundCount = 10,
                            averageResponseTimeMillis = mapOf(),
                            totalGameLengthMillis = 10,
                        ),
                )
            }

            is GameIntent.StartRound -> {
                GameState.RoundOn(
                    players = intent.message.players,
                    currentQuestion = intent.message.currentQuestion!!,
                    timeRemaining = intent.message.timeRemaining!!,
                    cursorPosition = intent.message.cursorPosition,
                )
            }

            else -> throw IllegalStateException("${intent::class.simpleName} couldn't be reduced when current state is EndOfRound")
        }

    private fun reduceGameOverState(
        currentState: GameState.GameOver,
        intent: GameIntent,
    ): GameState =
        when (intent) {
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

    fun processServerMessage(message: ServerMessage) {
        when (message) {
            // effects
            is TimeUpdate -> sideEffect(GameEffect.ShowTimeRemaining(message.remaining))
            is AnswerResult -> sideEffect(GameEffect.ReceiveAnswerResult(message))
            is PlayerDisconnected -> sideEffect(GameEffect.PlayerDisconnected(message))
            is Error -> sideEffect(GameEffect.ShowError(message.message))
            is PlayerReconnected -> sideEffect(GameEffect.PlayerReconnected(message))
            is RoomCreated -> sideEffect(GameEffect.RoomCreated(message))
            is RoomJoined -> sideEffect(GameEffect.RoomJoined(message))
            is RoundUpdate -> sideEffect(GameEffect.RoundUpdate(message))
            is TimeUp -> sideEffect(GameEffect.RoundTimeUp(message))

            // intents
            is Countdown -> reduce(GameIntent.Countdown(message))
            is RoomUpdate -> {
                if (message.state == RoomState.WAITING) {
                    reduce(GameIntent.Lobby(message))
                } else if (message.state == RoomState.PLAYING) {
                    reduce(GameIntent.StartRound(message))
                }

                Timber.tag("GameStateMachine: ").d("RoomUpdate: $message")
            }

            is RoundEnded -> reduce(GameIntent.RoundEnd(message))
            is GameOver -> reduce(GameIntent.GameOver(message))
            is RoomClosed -> reduce(GameIntent.CloseRoom(message))
        }
    }
}
