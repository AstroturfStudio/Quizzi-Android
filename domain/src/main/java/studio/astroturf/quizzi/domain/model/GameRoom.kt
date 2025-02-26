package studio.astroturf.quizzi.domain.model

data class GameRoom(
    val id: String,
    val name: String,
    val gameType: String,
    val category: String,
    val roomState: RoomState,
    val players: List<String>,
)
