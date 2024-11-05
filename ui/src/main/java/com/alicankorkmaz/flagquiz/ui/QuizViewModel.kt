package com.alicankorkmaz.flagquiz.ui


import LobbyUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicankorkmaz.flagquiz.domain.model.ClientQuestion
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
                        _uiState.update { currentState ->
                            currentState.copy(
                                currentQuestion = message.currentQuestion,
                                timeRemaining = message.timeRemaining,
                                gameState = message.gameState,
                                cursorPosition = message.cursorPosition,
                                error = null
                            )
                        }
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

                    is GameMessage.ErrorMessage -> {
                        _lobbyState.update { currentState ->
                            currentState.copy(
                                error = message.reason
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

                    else -> Unit
                }
            }
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

data class QuizUiState(
    val currentQuestion: ClientQuestion? = null,
    val timeRemaining: Long? = null,
    val gameState: GameState? = null,
    val lastAnswerResult: GameMessage.AnswerResult? = null,
    val showResult: Boolean = false,
    val error: String? = null,
    val score: Int = 0,
    val totalQuestions: Int = 10,
    val cursorPosition: Float = 0.5f,
    val winner: String? = null
) {
    val isGameOver: Boolean
        get() = gameState == GameState.FINISHED
}