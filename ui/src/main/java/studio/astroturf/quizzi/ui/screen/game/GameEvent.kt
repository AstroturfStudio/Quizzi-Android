package studio.astroturf.quizzi.ui.screen.game

sealed interface GameEvent {
    data class RoomCreated(val roomId: String) : GameEvent
    data class RoomJoined(val roomId: String) : GameEvent
    data class Error(val message: String) : GameEvent
}
