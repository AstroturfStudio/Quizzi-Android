package com.astroturf.quizzi.ui.screen.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astroturf.quizzi.domain.model.websocket.ClientMessage
import com.astroturf.quizzi.domain.model.websocket.ServerMessage
import com.astroturf.quizzi.domain.repository.QuizRepository
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

    private val _navigationEvent = Channel<String>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        repository.connect()
        viewModelScope.launch {
            getRooms()
        }
        observeMessages()
    }

    private suspend fun getRooms() {
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
                    _navigationEvent.send(message.roomId)
                }
                is ServerMessage.RoomJoined -> {
                    if (message.success) {
                        _navigationEvent.send(message.roomId)
                    }
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

    fun refresh() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                getRooms()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}