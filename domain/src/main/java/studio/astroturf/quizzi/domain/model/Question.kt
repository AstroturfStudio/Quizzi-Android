package studio.astroturf.quizzi.domain.model

open class Question(
    val content: String,
    val countryCode: String?,
    val options: List<Option>,
)
