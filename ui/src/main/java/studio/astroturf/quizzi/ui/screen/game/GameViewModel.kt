package studio.astroturf.quizzi.ui.screen.game


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.astroturf.quizzi.domain.gamestatemachine.GameStateMachine
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameIntent
import studio.astroturf.quizzi.domain.model.statemachine.GameState
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.AnswerResult
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.GameOver
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomUpdate
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoundResult
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.TimeUpdate
import studio.astroturf.quizzi.domain.repository.QuizRepository
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val gsm = GameStateMachine(viewModelScope)

    val gameFlow: StateFlow<GameState> = gsm.stateFlow

    val gameEffectFlow: Flow<GameEffect> = gsm.effectFlow

    private val _events = Channel<GameEvent>(Channel.CONFLATED)
    val events = _events.receiveAsFlow()

    init {
        repository.connect()
        observeGameMessages()
    }

    fun processIntent(intent: GameIntent) {
        gsm.reduce(intent)
    }

    private fun observeGameMessages() {
        viewModelScope.launch {
            var lastUpdateTime = 0L
            val minUpdateInterval = 16L // ~60 FPS

            repository.observeMessages()
                .collect { message ->
                    withContext(Dispatchers.Default) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastUpdateTime < minUpdateInterval) {
                            delay(minUpdateInterval - (currentTime - lastUpdateTime))
                        }

                        processServerMessage(message)
                        lastUpdateTime = System.currentTimeMillis()
                    }
                }
        }
    }

    private suspend fun processServerMessage(message: ServerMessage) {
        withContext(Dispatchers.Main) {
            when (message) {
                is TimeUpdate -> processIntent(GameIntent.TimeUpdate(message))
                is RoomUpdate -> handleRoomUpdateMessage(message)
                is GameOver -> processIntent(GameIntent.GameOver(message))
                is AnswerResult -> processIntent(GameIntent.AnswerResult(message))
                is RoundResult -> processIntent(GameIntent.RoundResult(message))
                else -> handleOtherMessages(message)
            }
        }
    }

    private fun handleRoomUpdateMessage(message: RoomUpdate) {
        if (message.state == RoomState.COUNTDOWN) {
            viewModelScope.launch {
                for (i in 3 downTo 1) {
                    processIntent(GameIntent.Countdown(timeRemaining = i))
                    delay(1000)
                }
            }
        } else if (message.state == RoomState.PLAYING) {
            processIntent(
                GameIntent.UpdateRoom(message)
            )
        }
    }

    fun submitAnswer(answer: Int) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            repository.sendMessage(ClientMessage.PlayerAnswer(answer))
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }

    private fun handleOtherMessages(message: ServerMessage) {
        when (message) {
            is ServerMessage.RoomCreated -> {
                _events.trySend(GameEvent.RoomCreated(message.roomId))
            }

            is ServerMessage.RoomJoined -> {
                _events.trySend(GameEvent.RoomJoined(message.roomId))
            }

            is ServerMessage.Error -> {
                _events.trySend(GameEvent.Error(message.message))
            }

            else -> Unit
        }
    }
}