package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
) 