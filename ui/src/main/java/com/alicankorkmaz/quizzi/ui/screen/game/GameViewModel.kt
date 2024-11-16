package com.alicankorkmaz.quizzi.ui.screen.game


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicankorkmaz.quizzi.domain.model.RoomState
import com.alicankorkmaz.quizzi.domain.model.websocket.ClientMessage
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.AnswerResult
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.GameOver
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.RoomUpdate
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.RoundResult
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.TimeUpdate
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        _uiState.update { current ->
            val playerMap = message.players.associate { it.id to it.name }
            current.copy(
                currentQuestion = message.currentQuestion,
                roomState = message.state,
                cursorPosition = message.cursorPosition,
                timeRemaining = message.timeRemaining,
                lastAnswer = null,
                hasAnswered = false,
                playerIdToNameMap = playerMap
            )
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
                winner = current.playerIdToNameMap[message.winnerPlayerId] ?: message.winnerPlayerId,
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
                current.copy(
                    showRoundResult = true,
                    correctAnswer = message.correctAnswer,
                    winnerPlayerName = message.winnerPlayerId,
                    isWinner = message.winnerPlayerId == current.playerId
                )
            }
            // Show round result for 2 seconds
            delay(2000)
            _uiState.update { current ->
                current.copy(
                    showRoundResult = false,
                    correctAnswer = null,
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