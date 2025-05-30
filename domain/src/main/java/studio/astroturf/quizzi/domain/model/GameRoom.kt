package studio.astroturf.quizzi.domain.model

data class GameRoom(
    val id: String,
    val name: String,
    val gameType: GameType,
    val category: Category,
    val roomState: RoomState,
    val players: List<String>,
)
