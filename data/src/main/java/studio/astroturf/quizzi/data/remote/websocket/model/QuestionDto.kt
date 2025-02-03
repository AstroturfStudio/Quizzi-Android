package studio.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.Question

@Serializable
data class QuestionDto(
    val imageCode: String?,
    val content: String,
    val options: List<OptionDto>,
) {
    fun toDomain() =
        Question(
            countryCode = imageCode,
            content = content,
            options = options.map { it.toDomain() },
        )
}
