package studio.astroturf.quizzi.domain.repository

import studio.astroturf.quizzi.domain.model.GameType
import studio.astroturf.quizzi.domain.result.QuizziResult

interface GameTypeRepository {
    suspend fun getGameTypes(): QuizziResult<List<GameType>>
}
