package studio.astroturf.quizzi.data.repository.auth

import studio.astroturf.quizzi.data.remote.rest.service.QuizziApiService
import studio.astroturf.quizzi.data.remote.websocket.model.PlayerDto
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.repository.AuthRepository
import studio.astroturf.quizzi.domain.result.QuizziResult
import studio.astroturf.quizzi.domain.result.map
import studio.astroturf.quizzi.domain.result.onSuccess
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val quizziApiService: QuizziApiService,
    ) : AuthRepository {
        private var currentPlayerDto: PlayerDto? = null

        override suspend fun login(playerId: String): QuizziResult<Player> =
            quizziApiService
                .login(playerId)
                .onSuccess { currentPlayerDto = it }
                .map { it.toDomain() }

        override suspend fun createPlayer(
            name: String,
            avatarUrl: String,
        ): QuizziResult<Player> =
            quizziApiService
                .createPlayer(name, avatarUrl)
                .onSuccess { currentPlayerDto = it }
                .map { it.toDomain() }

        override fun getCurrentPlayerId(): String? = currentPlayerDto?.id
    }
