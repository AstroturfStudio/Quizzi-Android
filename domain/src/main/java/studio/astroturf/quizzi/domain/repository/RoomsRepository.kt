package studio.astroturf.quizzi.domain.repository

import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.result.QuizziResult

interface RoomsRepository {
    suspend fun getRooms(): QuizziResult<List<GameRoom>>
}
