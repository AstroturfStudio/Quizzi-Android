package studio.astroturf.quizzi.ui.screen.rooms

sealed interface RoomIntent {
    object CreateRoom : RoomIntent

    data class JoinRoom(
        val roomId: String,
    ) : RoomIntent
}
