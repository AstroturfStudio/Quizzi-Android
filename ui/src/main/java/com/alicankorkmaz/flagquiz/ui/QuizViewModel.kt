package com.alicankorkmaz.flagquiz.ui


import LobbyUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicankorkmaz.flagquiz.domain.model.GameState
import com.alicankorkmaz.flagquiz.domain.model.websocket.GameMessage
import com.alicankorkmaz.flagquiz.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private val _lobbyState = MutableStateFlow(LobbyUiState())
    val lobbyState = _lobbyState.asStateFlow()

    init {
        repository.connect()
        observeQuizMessages()
    }

    private fun observeQuizMessages() {
        viewModelScope.launch {
            repository.observeMessages().collect { message ->
                when (message) {
                    is GameMessage.GameUpdate -> {
                        println("Cursor Position: ${message.cursorPosition}")
                        handleGameUpdate(message)
                    }

                    is GameMessage.RoomCreated -> {
                        println("Joined room: ${message.roomId}")
                        _lobbyState.update { currentState ->
                            currentState.copy(
                                currentRoom = message.roomId,
                                error = null
                            )
                        }
                    }

                    is GameMessage.JoinRoomResponse -> {
                        println("Joined room: ${message.roomId}")
                        _lobbyState.update { currentState ->
                            currentState.copy(
                                currentRoom = message.roomId,
                                error = null
                            )
                        }
                    }

                    is GameMessage.Error -> {
                        _lobbyState.update { currentState ->
                            currentState.copy(
                                error = message.message
                            )
                        }
                    }

                    is GameMessage.GameOver -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                gameState = GameState.FINISHED,
                                winner = message.winner
                            )
                        }
                    }

                    is GameMessage.TimeUpdate -> {
                        _uiState.update { currentState ->
                            currentState.copy(timeRemaining = message.timeRemaining)
                        }
                    }

                    is GameMessage.AnswerResult -> {
                        handleAnswerResult(message)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun handleAnswerResult(result: GameMessage.AnswerResult) {
        _uiState.update { currentState ->
            currentState.copy(
                lastAnswer = result,
                hasAnswered = true
            )
        }
    }

    private fun handleGameUpdate(update: GameMessage.GameUpdate) {
        _uiState.update { currentState ->
            currentState.copy(
                currentQuestion = update.currentQuestion,
                gameState = update.gameState,
                cursorPosition = update.cursorPosition,
                timeRemaining = update.timeRemaining,
                lastAnswer = null,
                hasAnswered = false
            )
        }
    }

    fun submitAnswer(answer: String) {
        repository.sendAnswer(answer)
        _uiState.update { it.copy(showResult = false) }
    }

    fun createRoom(playerName: String) {
        viewModelScope.launch {
            repository.createRoom(playerName = playerName)
        }
    }

    fun joinRoom(roomId: String, playerName: String) {
        viewModelScope.launch {
            repository.joinRoom(
                roomId = roomId,
                playerName = playerName
            )
        }
    }

    fun backToLobby() {
        viewModelScope.launch {
            repository.disconnect()
            _lobbyState.update {
                it.copy(currentRoom = null)
            }
            _uiState.update {
                QuizUiState()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}