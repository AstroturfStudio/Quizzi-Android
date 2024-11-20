package studio.astroturf.quizzi.ui.screen.rooms

sealed interface RoomsEvent {
    data class NavigateToRoom(val roomId: String) : RoomsEvent
}