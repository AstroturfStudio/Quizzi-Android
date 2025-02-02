package studio.astroturf.quizzi.ui.screen.create.category

data class CategoryUiModel(
    val categoryName: String,
    val isSelected: Boolean = false,
) {
    companion object {
        fun from(category: studio.astroturf.quizzi.domain.model.Category): CategoryUiModel =
            CategoryUiModel(
                categoryName = category.name,
            )
    }
}
