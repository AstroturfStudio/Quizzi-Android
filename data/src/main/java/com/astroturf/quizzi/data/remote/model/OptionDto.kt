package com.astroturf.quizzi.data.remote.model

import com.astroturf.quizzi.domain.model.Option
import kotlinx.serialization.Serializable

@Serializable
data class OptionDto(
    val id: Int,
    val value: String
) {
    fun toDomain() = Option(
        id = id,
        value = value
    )
}