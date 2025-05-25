package studio.astroturf.quizzi.data.remote.rest.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.domain.model.CategoryId

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
) {
    fun toDomain() =
        Category(
            id = CategoryId.fromId(id),
            name = name,
        )
}
