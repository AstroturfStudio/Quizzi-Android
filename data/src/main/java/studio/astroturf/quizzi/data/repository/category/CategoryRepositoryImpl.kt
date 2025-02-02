package studio.astroturf.quizzi.data.repository.category

import studio.astroturf.quizzi.data.remote.rest.service.QuizziApiService
import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.domain.repository.CategoryRepository
import studio.astroturf.quizzi.domain.result.QuizziResult
import studio.astroturf.quizzi.domain.result.map
import javax.inject.Inject

class CategoryRepositoryImpl
    @Inject
    constructor(
        private val quizziApiService: QuizziApiService,
    ) : CategoryRepository {
        override suspend fun getCategories(): QuizziResult<List<Category>> =
            quizziApiService
                .getCategories()
                .map {
                    it.categories.map { it.toDomain() }
                }
    }
