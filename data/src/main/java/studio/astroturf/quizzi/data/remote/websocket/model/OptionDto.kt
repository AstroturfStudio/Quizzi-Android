package studio.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.Option

@Serializable
data class OptionDto(
    val id: Int,
    val value: String,
) {
    fun toDomain() =
        Option(
            id = id,
            value = value,
        )
}
