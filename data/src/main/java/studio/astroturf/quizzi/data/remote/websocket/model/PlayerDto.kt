package studio.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.Player

@Serializable
data class PlayerDto(
    val id: String,
    val name: String,
    val avatarUrl: String
) {
    fun toDomain() = Player(
        id = id,
        name = name,
        avatarUrl = avatarUrl
    )
}