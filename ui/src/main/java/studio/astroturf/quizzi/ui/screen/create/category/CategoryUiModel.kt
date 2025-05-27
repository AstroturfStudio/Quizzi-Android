package studio.astroturf.quizzi.ui.screen.create.category

import androidx.annotation.StringRes
import studio.astroturf.quizzi.domain.model.Category

data class CategoryUiModel(
    val category: Category,
    @StringRes val categoryNameResId: Int,
    val isSelected: Boolean = false,
) {
    companion object {
        fun from(category: Category): CategoryUiModel =
            CategoryUiModel(
                category = category,
                categoryNameResId = category.id.getCategoryNameResId(),
            )
    }
}
