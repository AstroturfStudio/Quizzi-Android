package studio.astroturf.quizzi.ui.screen.game


import NavDestination
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.gamestatemachine.GameStateMachine
import studio.astroturf.quizzi.domain.model.RoomState
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameIntent
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
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
import studio.astroturf.quizzi.domain.repository.QuizRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: QuizRepository
) : ViewModel() {

    private val roomId: String? = savedStateHandle[NavDestination.Game.ARG_ROOM_ID]

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<GameUiEvent>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val gsm = GameStateMachine(viewModelScope)

    init {
        // Map domain state to UI state
        viewModelScope.launch {
            gsm.stateFlow
                .map { it.toUiState() }
                .catch { error ->
                    Timber.e("GameViewModel", "State mapping error", error)
                }
                .collect { uiState ->
                    _uiState.value = uiState
                }
        }

        // Handle game effects
        viewModelScope.launch {
            gsm.effectFlow.collect { effect ->
                handleGameEffect(effect)
            }
        }

        viewModelScope.launch {
            repository
                .observeMessages()
                .collect { message ->
                    processServerMessage(message)
                }
        }

        repository.connect()

        roomId?.let {
            joinRoom(it)
        } ?: createRoom()
    }

    private fun processServerMessage(message: ServerMessage) {
        when (message) {
            // effects
            is TimeUpdate -> gsm.sideEffect(GameEffect.ShowTimeRemaining(message.remaining))
            is AnswerResult -> gsm.sideEffect(GameEffect.ReceiveAnswerResult(message))
            is PlayerDisconnected -> gsm.sideEffect(GameEffect.PlayerDisconnected(message))
            is Error -> gsm.sideEffect(GameEffect.ShowError(message.message))
            is PlayerReconnected -> gsm.sideEffect(GameEffect.PlayerReconnected(message))
            is RoomCreated -> gsm.sideEffect(GameEffect.RoomCreated(message))
            is RoomJoined -> gsm.sideEffect(GameEffect.RoomJoined(message))
            is RoundUpdate -> gsm.sideEffect(GameEffect.RoundUpdate(message))

            // intents
            is Countdown -> gsm.reduce(GameIntent.Countdown(message))
            is RoomUpdate -> {
                if (message.state == RoomState.WAITING) {
                    gsm.reduce(GameIntent.Lobby(message))
                } else if (message.state == RoomState.PLAYING) {
                    gsm.reduce(GameIntent.StartRound(message))
                }

                Timber.tag("GameViewModel: ").d("RoomUpdate: $message")
            }

            is RoundEnded -> gsm.reduce(GameIntent.RoundEnd(message))
            is TimeUp -> gsm.reduce(GameIntent.RoundTimeUp(message))
            is GameOver -> gsm.reduce(GameIntent.GameOver(message))
            is RoomClosed -> gsm.reduce(GameIntent.CloseRoom(message))
        }
    }

    private suspend fun handleGameEffect(effect: GameEffect) {
        when (effect) {
            is GameEffect.NavigateTo -> {
                _uiEffect.send(GameUiEvent.NavigateTo(effect.destination))
            }

            is GameEffect.PlayerDisconnected -> {

            }

            is GameEffect.PlayerReconnected -> {

            }

            is GameEffect.ReceiveAnswerResult -> {

            }

            is GameEffect.RoomCreated -> {

            }

            is GameEffect.RoomJoined -> {

            }

            is GameEffect.RoundUpdate -> {

            }

            is GameEffect.ShowError -> {

            }

            is GameEffect.ShowTimeRemaining -> {
                val currentState = _uiState.value
                _uiState.value = when (currentState) {
                    is GameUiState.RoundOn -> {
                        currentState.copy(
                            timeRemainingInSeconds = effect.timeRemaining.toInt()
                        )
                    }

                    else -> currentState
                }
            }

            is GameEffect.ShowToast -> {

            }
        }
    }

    fun createRoom() {
        repository.sendMessage(ClientMessage.CreateRoom)
    }

    fun joinRoom(roomId: String) {
        repository.sendMessage(ClientMessage.JoinRoom(roomId))
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