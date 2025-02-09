package studio.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.PlayerInRoom
import studio.astroturf.quizzi.domain.model.PlayerState

@Serializable
data class PlayerInRoomDto(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val state: String,
) {
    fun toDomain() =
        PlayerInRoom(
            id = id,
            name = name,
            avatarUrl = avatarUrl,
            state = PlayerState.valueOf(state),
        )
}
