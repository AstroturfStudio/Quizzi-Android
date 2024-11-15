package com.alicankorkmaz.quizzi.ui.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicankorkmaz.quizzi.domain.model.websocket.ClientMessage
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomsUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<RoomsEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        repository.connect()
        getRooms()
        observeMessages()
    }

    private fun getRooms() = viewModelScope.launch {
        repository.getRooms()
            .onSuccess { rooms ->
                _uiState.update {
                    it.copy(
                        isConnected = true,
                        rooms = rooms
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isConnected = false,
                        error = error.message
                    )
                }
            }
    }

    private fun observeMessages() = viewModelScope.launch {
        repository.observeMessages().collect { message ->
            when (message) {
                is ServerMessage.RoomCreated -> {
                    _eventChannel.send(RoomsEvent.NavigateToRoom(message.roomId))
                }

                is ServerMessage.RoomJoined -> {
                    _eventChannel.send(RoomsEvent.NavigateToRoom(message.roomId))
                }

                else -> Unit
            }
        }
    }

    fun createRoom() {
        repository.sendMessage(ClientMessage.CreateRoom)
    }

    fun joinRoom(roomId: String) {
        repository.sendMessage(ClientMessage.JoinRoom(roomId))
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}