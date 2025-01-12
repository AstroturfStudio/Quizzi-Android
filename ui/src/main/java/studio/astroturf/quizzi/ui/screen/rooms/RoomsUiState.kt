package studio.astroturf.quizzi.ui.screen.rooms

import studio.astroturf.quizzi.domain.model.GameRoom

data class RoomsUiState(
    var currentUsername: String = "",
    val rooms: List<GameRoom> = emptyList(),
    val isConnected: Boolean = false,
    val error: String? = null,
)
