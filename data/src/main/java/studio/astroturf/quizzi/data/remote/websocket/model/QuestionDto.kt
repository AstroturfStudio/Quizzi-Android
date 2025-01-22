package studio.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.Question

@Serializable
data class QuestionDto(
    val imageUrl: String?,
    val content: String,
    val options: List<OptionDto>,
) {
    fun toDomain() =
        Question(
            countryCode = imageUrl?.let { extractCountryCode(imageUrl) },
            content = content,
            options = options.map { it.toDomain() },
        )

    private fun extractCountryCode(flagUrl: String): String =
        flagUrl
            .split("/")
            .last()
            .split(".")
            .first()
}
