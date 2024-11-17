package com.astroturf.quizzi.data.remote.websocket.model

import com.astroturf.quizzi.domain.model.Question
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDto(
    val imageUrl: String?,
    val content: String,
    val options: List<OptionDto>
) {
    fun toDomain() = Question(
        imageUrl = imageUrl,
        content = content,
        options = options.map { it.toDomain() }
    )
}