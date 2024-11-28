package studio.astroturf.quizzi.domain.gamestatemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import studio.astroturf.quizzi.domain.model.GameStatistics
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.domain.model.statemachine.Destination
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameIntent
import studio.astroturf.quizzi.domain.model.statemachine.GameState
import studio.astroturf.quizzi.domain.model.statemachine.StateMachine
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import timber.log.Timber

private const val TAG = "GameStateMachine"
private const val DEFAULT_ROUND_COUNT = 10
private const val CHANNEL_BUFFER_SIZE = Channel.BUFFERED

class GameStateMachine(
    private val coroutineScope: CoroutineScope,
) : StateMachine<GameState, GameIntent, GameEffect> {
    private val _stateFlow = MutableStateFlow<GameState>(GameState.Idle)
    val stateFlow = _stateFlow.asStateFlow()

    private val _effects = Channel<GameEffect>()
    val effects = _effects.receiveAsFlow()

    private val stateMutex = Mutex()
    private val messageChannel = Channel<ServerMessage>(CHANNEL_BUFFER_SIZE)

    init {
        initializeServerMessageProcessing()
    }

    private fun initializeServerMessageProcessing() {
        coroutineScope.launch {
            messageChannel.consumeAsFlow().collect { message ->
                processMessageSafely(message)
            }
        }
    }

    override fun getCurrentState(): GameState = _stateFlow.value

    override fun sideEffect(effect: GameEffect) {
        coroutineScope.launch {
            _effects.send(effect)
        }
    }

    override fun reduce(intent: GameIntent) {
        coroutineScope.launch {
            reduceWithLock(intent)
        }
    }

    private suspend fun reduceWithLock(intent: GameIntent) {
        stateMutex.withLock {
            val currentState = getCurrentState()
            logStateTransition(currentState, intent)

            val newState =
                try {
                    reduceState(currentState, intent)
                } catch (e: IllegalStateException) {
                    handleStateTransitionError(e, currentState)
                    currentState
                }

            if (newState != currentState) {
                logStateChange(currentState, newState)
            }
            _stateFlow.value = newState
        }
    }

    private fun reduceState(
        currentState: GameState,
        intent: GameIntent,
    ): GameState =
        when (currentState) {
            GameState.Idle -> reduceIdleState(intent)
            is GameState.Lobby -> reduceLobbyState(currentState, intent)
            is GameState.Starting -> reduceStartingState(intent)
            is GameState.RoundOn -> reduceRoundOnState(currentState, intent)
            is GameState.Paused -> reducePausedState(intent)
            is GameState.EndOfRound -> reduceEndOfRoundState(intent)
            is GameState.GameOver -> reduceGameOverState(intent)
        }

    private fun reduceIdleState(intent: GameIntent): GameState =
        when (intent) {
            is GameIntent.Lobby ->
                GameState.Lobby(
                    roomId = intent.message.toString(),
                    players = intent.message.players,
                )
            is GameIntent.CloseRoom, GameIntent.ExitGame -> throw NotImplementedError()
            else -> throw IllegalStateException(getInvalidTransitionMessage("Idle", intent))
        }

    private fun reduceLobbyState(
        currentState: GameState.Lobby,
        intent: GameIntent,
    ): GameState =
        when (intent) {
            is GameIntent.Countdown -> GameState.Starting(intent.message.remaining.toInt())
            is GameIntent.Lobby -> getCurrentState()
            is GameIntent.CloseRoom, GameIntent.ExitGame -> throw NotImplementedError()
            else -> throw IllegalStateException(getInvalidTransitionMessage("Lobby", intent))
        }

    private fun reduceStartingState(intent: GameIntent): GameState =
        when (intent) {
            is GameIntent.StartRound ->
                with(intent.message) {
                    GameState.RoundOn(
                        players = players,
                        currentQuestion = currentQuestion!!,
                        timeRemaining = timeRemaining!!,
                        cursorPosition = cursorPosition,
                    )
                }
            is GameIntent.Countdown -> GameState.Starting(intent.message.remaining.toInt())
            is GameIntent.CloseRoom, GameIntent.ExitGame -> throw NotImplementedError()
            else -> throw IllegalStateException(getInvalidTransitionMessage("Starting", intent))
        }

    private fun reduceRoundOnState(
        currentState: GameState.RoundOn,
        intent: GameIntent,
    ): GameState =
        when (intent) {
            GameIntent.ExitGame -> {
                handleExitGame()
            }
            is GameIntent.GameOver -> {
                createGameOverState(intent, currentState.players)
            }
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
            else -> {
                throw IllegalStateException(getInvalidTransitionMessage("RoundOn", intent))
            }
        }

    private fun reducePausedState(intent: GameIntent): GameState =
        when (intent) {
            is GameIntent.StartRound ->
                with(intent.message) {
                    GameState.RoundOn(
                        players = players,
                        currentQuestion = currentQuestion!!,
                        timeRemaining = timeRemaining!!,
                        cursorPosition = cursorPosition,
                    )
                }
            is GameIntent.CloseRoom, GameIntent.ExitGame -> throw NotImplementedError()
            else -> throw IllegalStateException(getInvalidTransitionMessage("Paused", intent))
        }

    private fun reduceEndOfRoundState(intent: GameIntent): GameState =
        when (intent) {
            is GameIntent.GameOver -> createGameOverState(intent)
            is GameIntent.StartRound ->
                with(intent.message) {
                    GameState.RoundOn(
                        players = players,
                        currentQuestion = currentQuestion!!,
                        timeRemaining = timeRemaining!!,
                        cursorPosition = cursorPosition,
                    )
                }
            is GameIntent.CloseRoom, GameIntent.ExitGame -> throw NotImplementedError()
            else -> throw IllegalStateException(getInvalidTransitionMessage("EndOfRound", intent))
        }

    private fun reduceGameOverState(intent: GameIntent): GameState =
        when (intent) {
            GameIntent.ExitGame -> handleExitGame()
            is GameIntent.CloseRoom -> handleCloseRoom(intent)
            else -> throw IllegalStateException(getInvalidTransitionMessage("GameOver", intent))
        }

    fun processServerMessage(message: ServerMessage) {
        coroutineScope.launch {
            messageChannel.send(message)
        }
    }

    private suspend fun processMessageSafely(message: ServerMessage) {
        Timber.tag(TAG).d("Processing server message: ${message::class.simpleName}")

        try {
            stateMutex.withLock {
                processMessage(message)
            }
        } catch (e: Exception) {
            handleMessageProcessingError(e, message)
        }
    }

    private fun processMessage(message: ServerMessage) {
        when (message) {
            // Effects
            is ServerMessage.TimeUpdate -> sideEffect(GameEffect.ShowTimeRemaining(message.remaining))
            is ServerMessage.AnswerResult -> sideEffect(GameEffect.ReceiveAnswerResult(message))
            is ServerMessage.PlayerDisconnected -> sideEffect(GameEffect.PlayerDisconnected(message))
            is ServerMessage.Error -> sideEffect(GameEffect.ShowError(message.message))
            is ServerMessage.PlayerReconnected -> sideEffect(GameEffect.PlayerReconnected(message))
            is ServerMessage.RoomCreated -> sideEffect(GameEffect.RoomCreated(message))
            is ServerMessage.RoomJoined -> sideEffect(GameEffect.RoomJoined(message))
            is ServerMessage.RoundUpdate -> sideEffect(GameEffect.RoundUpdate(message))
            is ServerMessage.TimeUp -> sideEffect(GameEffect.RoundTimeUp(message))
            is ServerMessage.RoundEnded -> sideEffect(GameEffect.RoundEnd(message))

            // State transitions
            is ServerMessage.Countdown -> handleCountdown(message)
            is ServerMessage.RoomUpdate -> handleRoomUpdate(message)
            is ServerMessage.GameOver -> reduce(GameIntent.GameOver(message))
            is ServerMessage.RoomClosed -> reduce(GameIntent.CloseRoom(message))
        }
    }

    private fun handleCountdown(message: ServerMessage.Countdown) {
        Timber.tag(TAG).d("Processing Countdown message with remaining: ${message.remaining}")
        reduce(GameIntent.Countdown(message))
    }

    private fun handleRoomUpdate(message: ServerMessage.RoomUpdate) {
        Timber.tag(TAG).d("Processing RoomUpdate message with state: ${message.state}")
        when (message.state) {
            RoomState.WAITING -> reduce(GameIntent.Lobby(message))
            RoomState.PLAYING -> handlePlayingState(message)
            else -> {
                Timber.tag(TAG).w("Unhandled room state: ${message.state}")
                // Current state remains unchanged
            }
        }
    }

    private fun handlePlayingState(message: ServerMessage.RoomUpdate) {
        val currentState = getCurrentState()
        if (currentState !is GameState.EndOfRound) {
            Timber.tag(TAG).w(
                "Received StartRound while in ${currentState::class.simpleName}. " +
                    "Expected EndOfRound state.",
            )
        }
        reduce(GameIntent.StartRound(message))
    }

    private fun createGameOverState(
        intent: GameIntent.GameOver,
        players: List<Player>,
    ): GameState.GameOver {
        val winner = players.first { it.id == intent.message.winnerPlayerId }
        return GameState.GameOver(
            winner = winner,
            statistics = createDefaultGameStatistics(),
        )
    }

    private fun createGameOverState(intent: GameIntent.GameOver): GameState.GameOver {
        val winner =
            Player(
                id = intent.message.winnerPlayerId,
                name = intent.message.winnerPlayerId,
                avatarUrl = "",
            )
        return GameState.GameOver(
            winner = winner,
            statistics = createDefaultGameStatistics(),
        )
    }

    private fun handleExitGame(): GameState {
        sideEffect(GameEffect.NavigateTo(Destination.Rooms))
        return GameState.Idle
    }

    private fun handleCloseRoom(intent: GameIntent.CloseRoom): GameState {
        sideEffect(GameEffect.ShowError(intent.message.reason))
        return GameState.Idle
    }

    private fun createDefaultGameStatistics() =
        GameStatistics(
            roundCount = DEFAULT_ROUND_COUNT,
            averageResponseTimeMillis = mapOf(),
            totalGameLengthMillis = DEFAULT_ROUND_COUNT.toLong(),
        )

    private fun handleStateTransitionError(
        e: IllegalStateException,
        currentState: GameState,
    ) {
        Timber.tag(TAG).e(e, "State transition error")
        sideEffect(GameEffect.ShowError("Invalid game state transition"))
    }

    private fun handleMessageProcessingError(
        e: Exception,
        message: ServerMessage,
    ) {
        Timber.tag(TAG).e(e, "Error processing message: $message")
        sideEffect(GameEffect.ShowError("Error processing game state: ${e.message}"))
    }

    private fun logStateTransition(
        currentState: GameState,
        intent: GameIntent,
    ) {
        Timber.tag(TAG).d(
            "Reducing intent ${intent::class.simpleName} in state ${currentState::class.simpleName}",
        )
    }

    private fun logStateChange(
        currentState: GameState,
        newState: GameState,
    ) {
        Timber.tag(TAG).d(
            "State transition: ${currentState::class.simpleName} -> ${newState::class.simpleName}",
        )
    }

    private fun getInvalidTransitionMessage(
        stateName: String,
        intent: GameIntent,
    ): String = "${intent::class.simpleName} couldn't be reduced when current state is $stateName"
}
