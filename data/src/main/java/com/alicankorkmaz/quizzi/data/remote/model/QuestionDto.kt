package com.alicankorkmaz.quizzi.data.remote.model

import com.alicankorkmaz.quizzi.domain.model.Question
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