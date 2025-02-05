package studio.astroturf.quizzi.ui.screen.create.category

import studio.astroturf.quizzi.domain.model.Category

data class CategoryUiModel(
    val category: Category,
    val isSelected: Boolean = false,
) {
    companion object {
        fun from(category: Category): CategoryUiModel =
            CategoryUiModel(
                category = category,
            )
    }
}
