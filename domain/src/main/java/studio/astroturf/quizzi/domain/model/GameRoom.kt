package studio.astroturf.quizzi.domain.model

data class GameRoom(
    val id: String,
    val playerCount: Int,
    val roomState: RoomState,
    val players: List<String> // oyuncu isimleri
)
