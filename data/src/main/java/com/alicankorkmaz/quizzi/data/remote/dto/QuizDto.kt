package com.alicankorkmaz.quizzi.data.remote.dto

import com.alicankorkmaz.quizzi.domain.model.Quiz
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizDto(
    @SerialName("id") val id: Int,
    @SerialName("question") val question: String,
    @SerialName("correctFlag") val correctFlag: FlagDto,
    @SerialName("options") val options: List<FlagDto>
) {
    fun toQuiz(): Quiz {
        return Quiz(
            id = id,
            question = question,
            correctFlag = correctFlag.toFlag(),
            options = options.map { it.toFlag() }
        )
    }
}