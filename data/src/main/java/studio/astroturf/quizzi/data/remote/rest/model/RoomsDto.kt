package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.model.RoomState

@Serializable
data class RoomsDto(
    val rooms: List<GameRoomDto>,
)

@Serializable
data class GameRoomDto(
    val id: String,
    val name: String,
    val playerCount: Int,
    val gameType: String,
    val category: String,
    val roomState: RoomState,
    val players: List<String>,
) {
    fun toDomain() =
        GameRoom(
            id = id,
            name = name,
            gameType = gameType,
            category = category,
            roomState = roomState,
            players = players,
        )
}
