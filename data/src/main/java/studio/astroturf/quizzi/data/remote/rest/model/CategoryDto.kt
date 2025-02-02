package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.Category

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
) {
    fun toDomain() =
        Category(
            id = id,
            name = name,
        )
}
