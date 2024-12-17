package studio.astroturf.quizzi.domain.repository

import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.result.QuizziResult

interface AuthRepository {
    suspend fun login(playerId: String): QuizziResult<Player>

    suspend fun createPlayer(
        name: String,
        avatarUrl: String,
    ): QuizziResult<Player>

    fun getCurrentPlayerId(): String?
}
