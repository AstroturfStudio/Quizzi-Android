package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.model.RoomState

@OptIn(InternalSerializationApi::class)
@Serializable
data class RoomsDto(
    val rooms: List<GameRoomDto>,
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class GameRoomDto(
    val id: String,
    val name: String,
    val playerCount: Int,
    val gameType: String,
    val category: CategoryDto,
    val roomState: RoomState,
    val players: List<String>,
) {
    fun toDomain() =
        GameRoom(
            id = id,
            name = name,
            gameType = gameType,
            category = category.toDomain(),
            roomState = roomState,
            players = players,
        )
}
