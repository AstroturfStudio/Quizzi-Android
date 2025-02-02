package studio.astroturf.quizzi.domain.repository

import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.domain.result.QuizziResult

interface CategoryRepository {
    suspend fun getCategories(): QuizziResult<List<Category>>
}
