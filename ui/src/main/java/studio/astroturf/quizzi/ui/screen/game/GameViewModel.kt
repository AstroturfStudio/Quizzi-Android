package studio.astroturf.quizzi.ui.screen.game


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.gamestatemachine.GameStateMachine
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameIntent
import studio.astroturf.quizzi.domain.model.statemachine.GameState
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.AnswerResult
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.Error
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.GameOver
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomClosed
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomCreated
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomJoined
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoomUpdate
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.RoundResult
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage.TimeUp
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

    init {
        repository.connect()
        viewModelScope.launch {
            repository
                .observeMessages()
                .collect { message ->
                    processServerMessage(message)
                }
        }
    }

    private fun processServerMessage(message: ServerMessage) {
        when (message) {
            is TimeUpdate -> gsm.sideEffect(GameEffect.ShowTimeRemaining(message.remaining))
            is RoomUpdate -> {
                if (message.state == RoomState.COUNTDOWN) {
                    for (i in 3..1) {
                        gsm.reduce(GameIntent.Initialize(message.copy(timeRemaining = i.toLong())))
                    }
                } else if (message.state == RoomState.PLAYING) {
                    gsm.reduce(GameIntent.Playing(message))
                }
            }

            is GameOver -> gsm.reduce(GameIntent.GameOver(message))
            is AnswerResult -> gsm.sideEffect(GameEffect.ReceiveAnswerResult(message))
            is RoundResult -> gsm.reduce(GameIntent.RoundCompleted(message))
            is RoomCreated -> gsm.reduce(GameIntent.RoomCreated(message))
            is RoomJoined -> gsm.reduce(GameIntent.RoomJoined(message))
            is Error -> gsm.sideEffect(GameEffect.ShowError(message.message))
            // is PlayerDisconnected -> gsm.reduce(GameIntent.PlayerDisconnected(message))
            // is PlayerReconnected -> gsm.reduce(GameIntent.PlayerReconnected(message))
            is RoomClosed -> gsm.reduce(GameIntent.CloseRoom(message))
            is TimeUp -> gsm.reduce(GameIntent.RoundTimeUp(message))
            else -> {}
        }
    }

    fun submitAnswer(answer: Int) {
        viewModelScope.launch {
            repository.sendMessage(ClientMessage.PlayerAnswer(answer))
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}