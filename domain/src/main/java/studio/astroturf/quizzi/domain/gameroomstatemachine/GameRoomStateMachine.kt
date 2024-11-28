package studio.astroturf.quizzi.domain.gameroomstatemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomStateChanger
import studio.astroturf.quizzi.domain.model.statemachine.GameRoomStateUpdater
import studio.astroturf.quizzi.domain.model.statemachine.StateMachine
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.repository.QuizRepository
import timber.log.Timber

private const val TAG = "GameRoomStateMachine"

class GameRoomStateMachine(
    private val coroutineScope: CoroutineScope,
    private val repository: QuizRepository,
) : StateMachine<GameRoomState, GameRoomStateChanger, GameRoomStateUpdater> {
    private val _state = MutableStateFlow<GameRoomState>(GameRoomState.Idle)
    val state = _state.asStateFlow()

    private val _effects = Channel<GameRoomStateUpdater>()
    val effects = _effects.receiveAsFlow()

    private val stateMutex = Mutex()

    init {
        coroutineScope.launch {
            repository
                .observeMessages()
                .buffer()
                .collect { message ->
                    processMessageSafely(message)
                }
        }
    }

    override fun getCurrentState(): GameRoomState = _state.value

    override fun sideEffect(effect: GameRoomStateUpdater) {
        coroutineScope.launch {
            _effects.send(effect)
        }
    }

    private suspend fun processMessageSafely(message: ServerMessage) {
        Timber.tag(TAG).d("Processing server message: ${message::class.simpleName}")

        try {
            stateMutex.withLock {
                processMessage(message)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error processing message: $message")
        }
    }

    private fun processMessage(message: ServerMessage) {
        when (message) {
            is ServerMessage.RoomUpdate -> reduce(GameRoomStateChanger.RoomUpdate(message))

            else -> processGameRoomStateUpdater(message)
        }
    }

    private fun processGameRoomStateUpdater(message: ServerMessage) {
        when (message) {
            is ServerMessage.AnswerResult -> sideEffect(GameRoomStateUpdater.ReceiveAnswerResult(message))
            is ServerMessage.PlayerDisconnected -> sideEffect(GameRoomStateUpdater.PlayerDisconnected(message))
            is ServerMessage.Error -> sideEffect(GameRoomStateUpdater.Error(message))
            is ServerMessage.PlayerReconnected -> sideEffect(GameRoomStateUpdater.PlayerReconnected(message))
            is ServerMessage.RoomCreated -> sideEffect(GameRoomStateUpdater.RoomCreated(message))
            is ServerMessage.JoinedRoom -> sideEffect(GameRoomStateUpdater.RoomJoined(message))
            is ServerMessage.RoundStarted -> sideEffect(GameRoomStateUpdater.RoundStarted(message))
            is ServerMessage.TimeUp -> sideEffect(GameRoomStateUpdater.RoundTimeUp(message))
            is ServerMessage.RoundEnded -> sideEffect(GameRoomStateUpdater.RoundEnd(message))
            is ServerMessage.CountdownTimeUpdate -> sideEffect(GameRoomStateUpdater.Countdown(message))
            is ServerMessage.GameOver -> sideEffect(GameRoomStateUpdater.GameRoomOver(message))
            is ServerMessage.TimeUpdate -> sideEffect(GameRoomStateUpdater.RoundTimeUpdate(message))
            is ServerMessage.RoomClosed -> sideEffect(GameRoomStateUpdater.CloseRoom(message))

            else -> return
        }
    }

    override fun reduce(intent: GameRoomStateChanger) {
        if (intent is GameRoomStateChanger.RoomUpdate) {
            coroutineScope.launch {
                reduceWithLock(intent)
            }
        }
    }

    private suspend fun reduceWithLock(intent: GameRoomStateChanger.RoomUpdate) {
        stateMutex.withLock {
            val currentState = getCurrentState()
            logStateTransition(currentState, intent)

            val newState =
                try {
                    reduceState(currentState, intent)
                } catch (e: IllegalStateException) {
                    Timber.tag(TAG).e(e, "State transition error")
                    currentState
                }

            if (newState != currentState) {
                logStateChange(currentState, newState)
            }
            _state.value = newState
        }
    }

    private fun reduceState(
        currentState: GameRoomState,
        intent: GameRoomStateChanger.RoomUpdate,
    ): GameRoomState =
        when (currentState) {
            is GameRoomState.Idle -> reduceIdleState(currentState, intent.message)
            is GameRoomState.Waiting -> reduceWaitingState(currentState, intent.message)
            is GameRoomState.Countdown -> reduceStartingState(currentState, intent.message)
            is GameRoomState.Playing -> reducePlayingState(currentState, intent.message)
            is GameRoomState.Paused -> reducePausedState(currentState, intent.message)
            is GameRoomState.Closed -> reduceClosedState(currentState, intent.message)
        }

    private fun reduceIdleState(
        currentState: GameRoomState.Idle,
        updateMessage: ServerMessage.RoomUpdate,
    ): GameRoomState =
        when (updateMessage.state) {
            RoomState.WAITING -> GameRoomState.Waiting(updateMessage.players)
            else -> throw IllegalStateException(getInvalidTransitionMessage("Idle", updateMessage))
        }

    private fun reduceWaitingState(
        currentState: GameRoomState.Waiting,
        updateMessage: ServerMessage.RoomUpdate,
    ): GameRoomState =
        when (updateMessage.state) {
            RoomState.COUNTDOWN -> GameRoomState.Countdown
            else -> throw IllegalStateException(getInvalidTransitionMessage("Waiting", updateMessage))
        }

    private fun reduceStartingState(
        currentState: GameRoomState.Countdown,
        updateMessage: ServerMessage.RoomUpdate,
    ): GameRoomState =
        when (updateMessage.state) {
            RoomState.PLAYING -> GameRoomState.Playing(updateMessage.players)
            else -> throw IllegalStateException(getInvalidTransitionMessage("Starting", updateMessage))
        }

    private fun reducePlayingState(
        currentState: GameRoomState.Playing,
        updateMessage: ServerMessage.RoomUpdate,
    ): GameRoomState =
        when (updateMessage.state) {
            RoomState.CLOSED -> GameRoomState.Closed(updateMessage.players)
            else -> throw IllegalStateException(getInvalidTransitionMessage("Playing", updateMessage))
        }

    private fun reducePausedState(
        currentState: GameRoomState.Paused,
        updateMessage: ServerMessage.RoomUpdate,
    ): GameRoomState =
        when (updateMessage.state) {
            RoomState.CLOSED -> GameRoomState.Closed(updateMessage.players)
            else -> throw IllegalStateException(getInvalidTransitionMessage("Paused", updateMessage))
        }

    private fun reduceClosedState(
        currentState: GameRoomState.Closed,
        updateMessage: ServerMessage.RoomUpdate,
    ): GameRoomState =
        when (updateMessage.state) {
            else -> throw IllegalStateException(getInvalidTransitionMessage("Closed", updateMessage))
        }

    private fun logStateTransition(
        currentState: GameRoomState,
        intent: GameRoomStateChanger,
    ) {
        Timber.tag(TAG).d(
            "Reducing intent ${intent::class.simpleName} in state ${currentState::class.simpleName}",
        )
    }

    private fun logStateChange(
        currentState: GameRoomState,
        newState: GameRoomState,
    ) {
        Timber.tag(TAG).d(
            "State transition: ${currentState::class.simpleName} -> ${newState::class.simpleName}",
        )
    }

    private fun getInvalidTransitionMessage(
        stateName: String,
        intent: ServerMessage.RoomUpdate,
    ): String = "${intent.state::class.simpleName} couldn't be reduced when current state is $stateName"
}
