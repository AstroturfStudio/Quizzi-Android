package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesDto(
    @SerialName("categories")
    val categories: Set<CategoryDto>,
)
