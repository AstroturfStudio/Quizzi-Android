package com.alicankorkmaz.quizzi.ui.lobby

data class LobbyUiState(
    val isConnected: Boolean = false,
    val rooms: List<String> = emptyList(),
    val error: String? = null,
    val currentRoom: String? = null,
    val players: List<String> = emptyList()
) 