package com.alicankorkmaz.quizzi.ui.game


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicankorkmaz.quizzi.domain.model.RoomState
import com.alicankorkmaz.quizzi.domain.model.websocket.ClientMessage
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.AnswerResult
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.Error
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.GameOver
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.RoomCreated
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.RoomJoined
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.RoomUpdate
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.RoundResult
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage.TimeUpdate
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import com.alicankorkmaz.quizzi.ui.lobby.LobbyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
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
                    is RoomUpdate -> {
                        println("Cursor Position: ${message.cursorPosition}")
                        handleRoomUpdate(message)
                    }

                    is RoomCreated -> {
                        println("Joined room: ${message.roomId}")
                        _lobbyState.update { currentState ->
                            currentState.copy(
                                currentRoom = message.roomId,
                                error = null
                            )
                        }
                    }

                    is RoomJoined -> {
                        println("Joined room: ${message.roomId}")
                        _lobbyState.update { currentState ->
                            currentState.copy(
                                currentRoom = message.roomId,
                                error = null
                            )
                        }
                    }

                    is Error -> {
                        _lobbyState.update { currentState ->
                            currentState.copy(
                                error = message.message
                            )
                        }
                    }

                    is GameOver -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                roomState = RoomState.FINISHED,
                                winner = message.winnerPlayerId
                            )
                        }
                    }

                    is TimeUpdate -> {
                        _uiState.update { currentState ->
                            currentState.copy(timeRemaining = message.remaining)
                        }
                    }

                    is AnswerResult -> {
                        handleAnswerResult(message)
                    }

                    is RoundResult -> {
                        handleRoundResult(message)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun handleAnswerResult(result: AnswerResult) {
        _uiState.update { currentState ->
            currentState.copy(
                lastAnswer = result,
                hasAnswered = true
            )
        }
    }

    private fun handleRoomUpdate(update: RoomUpdate) {
        _uiState.update { currentState ->
            currentState.copy(
                currentQuestion = update.currentQuestion,
                roomState = update.state,
                cursorPosition = update.cursorPosition,
                timeRemaining = update.timeRemaining,
                lastAnswer = null,
                hasAnswered = false
            )
        }
    }

    private fun handleRoundResult(result: RoundResult) {
        Log.d("QuizViewModel", "Round sonucu alındı: $result")
        _uiState.update { currentState ->
            currentState.copy(
                showRoundResult = true,
                correctAnswer = result.correctAnswer,
                winnerPlayerName = result.winnerPlayerId,
                isWinner = result.winnerPlayerId == currentState.playerId
            ).also {
                Log.d("QuizViewModel", "showRoundResult: ${it.showRoundResult}")
            }
        }

        viewModelScope.launch {
            delay(2000)
            _uiState.update { it.copy(showRoundResult = false) }
            Log.d("QuizViewModel", "Round sonucu gizlendi")
        }
    }

    fun submitAnswer(answer: Int) {
        repository.sendMessage(ClientMessage.PlayerAnswer(answer))
        _uiState.update { it.copy(showResult = false) }
    }

    fun createRoom() {
        repository.sendMessage(ClientMessage.CreateRoom)
    }

    fun joinRoom(roomId: String) {
        repository.sendMessage(ClientMessage.JoinRoom(roomId))
    }

    fun backToLobby() {
        repository.disconnect()
        _lobbyState.update {
            it.copy(currentRoom = null)
        }
        _uiState.update {
            GameUiState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}