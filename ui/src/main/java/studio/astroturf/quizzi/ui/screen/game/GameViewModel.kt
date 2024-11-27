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
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.repository.QuizRepository
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
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

    private val _uiEventChannel = Channel<GameUiEvent>()
    val uiEventFlow = _uiEventChannel.receiveAsFlow()

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
                    gsm.processServerMessage(message)
                }
        }

        repository.connect()

        roomId?.let {
            joinRoom(it)
        } ?: createRoom()
    }


    private suspend fun handleGameEffect(effect: GameEffect) {
        when (effect) {
            is GameEffect.NavigateTo -> {
                _uiEventChannel.send(GameUiEvent.NavigateTo(effect.destination))
            }

            is GameEffect.PlayerDisconnected -> {

            }

            is GameEffect.PlayerReconnected -> {

            }

            is GameEffect.ReceiveAnswerResult -> {
                val currentState = _uiState.value
                if (currentState is GameUiState.RoundOn) {
                    _uiState.value = currentState.copy(
                        playerRoundResult = PlayerRoundResult(
                            answerId = effect.answerResult.answer,
                            isCorrect = effect.answerResult.correct
                        )
                    )
                }
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

            is GameEffect.RoundTimeUp -> {

            }
        }
    }

    fun createRoom() {
        repository.sendMessage(ClientMessage.CreateRoom)
    }

    fun joinRoom(roomId: String) {
        repository.sendMessage(ClientMessage.JoinRoom(roomId))
    }

    fun submitAnswer(answerId: Int) {
        viewModelScope.launch {
            // Update UI state to show selected answer
            val currentState = _uiState.value
            if (currentState is GameUiState.RoundOn) {
                _uiState.value = currentState.copy(
                    selectedAnswerId = answerId
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
}