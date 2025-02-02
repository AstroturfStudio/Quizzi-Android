package studio.astroturf.quizzi.data.remote.rest.service

import studio.astroturf.quizzi.data.exceptionhandling.mapToQuizziException
import studio.astroturf.quizzi.data.remote.rest.api.QuizziApi
import studio.astroturf.quizzi.data.remote.rest.model.CategoriesDto
import studio.astroturf.quizzi.data.remote.rest.model.CreatePlayerRequestDto
import studio.astroturf.quizzi.data.remote.rest.model.GameTypesDto
import studio.astroturf.quizzi.data.remote.rest.model.LoginRequestDto
import studio.astroturf.quizzi.data.remote.rest.model.RoomsDto
import studio.astroturf.quizzi.data.remote.websocket.model.PlayerDto
import studio.astroturf.quizzi.domain.result.QuizziResult
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizziApiService
    @Inject
    constructor(
        private val api: QuizziApi,
    ) {
        suspend fun login(playerId: String): QuizziResult<PlayerDto> =
            try {
                val request = LoginRequestDto(id = playerId)
                val response = api.login(request)
                QuizziResult.success(response)
            } catch (e: Exception) {
                Timber.e(e, "Login failed for player: $playerId")
                QuizziResult.failure(e.mapToQuizziException())
            }

        suspend fun createPlayer(
            name: String,
            avatarUrl: String,
        ): QuizziResult<PlayerDto> =
            try {
                val request = CreatePlayerRequestDto(name = name, avatarUrl = avatarUrl)
                val response = api.createPlayer(request)
                QuizziResult.success(response)
            } catch (e: Exception) {
                Timber.e(e, "Create player failed for name: $name")
                QuizziResult.failure(e.mapToQuizziException())
            }

        suspend fun getRooms(): QuizziResult<RoomsDto> =
            try {
                val response = api.getRooms()
                QuizziResult.success(response)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get rooms")
                QuizziResult.failure(e.mapToQuizziException())
            }

        suspend fun getGameTypes(): QuizziResult<GameTypesDto> =
            try {
                val response = api.getGameTypes()
                QuizziResult.success(response)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get game types")
                QuizziResult.failure(e.mapToQuizziException())
            }

        suspend fun getCategories(): QuizziResult<CategoriesDto> =
            try {
                val response = api.getCategories()
                QuizziResult.success(response)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get categories")
                QuizziResult.failure(e.mapToQuizziException())
            }
    }
