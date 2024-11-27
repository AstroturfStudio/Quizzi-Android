package studio.astroturf.quizzi.domain.model

data class GameRoom(
    val id: String,
    val roomState: RoomState,
    val players: List<String>,
)
