package studio.astroturf.quizzi.ui.screen.game


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.astroturf.quizzi.domain.model.RoomState
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

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    init {
        repository.connect()
        observeGameMessages()
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

                        processMessage(message)
                        lastUpdateTime = System.currentTimeMillis()
                    }
                }
        }
    }

    private suspend fun processMessage(message: ServerMessage) {
        withContext(Dispatchers.Main) {
            when (message) {
                is TimeUpdate -> updateTimeState(message)
                is RoomUpdate -> updateRoomState(message)
                is GameOver -> handleGameOver(message)
                is AnswerResult -> handleAnswerResult(message)
                is RoundResult -> handleRoundResult(message)
                else -> handleOtherMessages(message)
            }
        }
    }

    private fun updateTimeState(message: TimeUpdate) {
        _uiState.update { current ->
            current.copy(timeRemaining = message.remaining)
        }
    }

    private fun updateRoomState(message: RoomUpdate) {
        viewModelScope.launch {
            _uiState.update { current ->
                val playerMap = message.players.associate { it.id to it.name }

                if (message.state == RoomState.COUNTDOWN && !current.showCountdown) {
                    // Countdown başlat
                    startCountdown()
                }

                current.copy(
                    currentQuestion = message.currentQuestion,
                    roomState = message.state,
                    cursorPosition = message.cursorPosition,
                    timeRemaining = message.timeRemaining,
                    lastAnswer = null,
                    hasAnswered = false,
                    playerIdToNameMap = playerMap,
                )
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            _uiState.update { it.copy(showCountdown = true, countdown = 3) }

            for (i in 3 downTo 1) {
                _uiState.update { it.copy(countdown = i) }
                delay(1000)
            }

            _uiState.update { it.copy(showCountdown = false, countdown = 0) }
        }
    }

    fun submitAnswer(answer: Int) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            repository.sendMessage(ClientMessage.PlayerAnswer(answer))
            _uiState.value = _uiState.value.copy(showResult = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }

    private fun handleGameOver(message: GameOver) {
        _uiState.update { current ->
            current.copy(
                roomState = RoomState.FINISHED,
                winner = current.playerIdToNameMap[message.winnerPlayerId]
                    ?: message.winnerPlayerId,
                isWinner = message.winnerPlayerId == current.playerId
            )
        }
    }

    private fun handleAnswerResult(message: AnswerResult) {
        _uiState.update { current ->
            current.copy(
                lastAnswer = message,
                hasAnswered = message.playerId == current.playerId,
                showResult = true
            )
        }
    }

    private fun handleRoundResult(message: RoundResult) {
        viewModelScope.launch {
            _uiState.update { current ->
                val correctAnswerText = current.currentQuestion?.let { question ->
                    question.options.find { it.id == message.correctAnswer }?.value
                }

                val winnerName = message.winnerPlayerId?.let { winnerId ->
                    current.playerIdToNameMap[winnerId] ?: winnerId
                }

                current.copy(
                    showRoundResult = true,
                    correctAnswer = message.correctAnswer,
                    correctAnswerText = correctAnswerText,
                    winnerPlayerName = winnerName,
                    isWinner = message.winnerPlayerId == current.playerId
                )
            }

            delay(2000)
            _uiState.update { current ->
                current.copy(
                    showRoundResult = false,
                    correctAnswerText = null,
                    winnerPlayerName = null
                )
            }
        }
    }

    private fun handleOtherMessages(message: ServerMessage) {
        when (message) {
            is ServerMessage.RoomCreated -> {
                _uiState.update { current ->
                    current.copy(
                        roomId = message.roomId,
                        error = null
                    )
                }
            }

            is ServerMessage.RoomJoined -> {
                _uiState.update { current ->
                    current.copy(
                        roomId = message.roomId,
                        error = null
                    )
                }
            }

            is ServerMessage.Error -> {
                _uiState.update { current ->
                    current.copy(error = message.message)
                }
            }

            else -> Unit
        }
    }
}