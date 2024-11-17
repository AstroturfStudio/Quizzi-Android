package com.astroturf.quizzi.ui.screen.rooms

import com.astroturf.quizzi.domain.model.GameRoom

data class RoomsUiState(
    val rooms: List<GameRoom> = emptyList(),
    val isConnected: Boolean = false,
    val error: String? = null
)
