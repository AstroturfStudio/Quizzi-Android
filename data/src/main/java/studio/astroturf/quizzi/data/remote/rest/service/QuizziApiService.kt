package studio.astroturf.quizzi.data.remote.rest.service

import retrofit2.HttpException
import studio.astroturf.quizzi.data.remote.rest.api.QuizziApi
import studio.astroturf.quizzi.data.remote.rest.model.CreatePlayerRequestDto
import studio.astroturf.quizzi.data.remote.rest.model.LoginRequestDto
import studio.astroturf.quizzi.data.remote.rest.model.RoomsDto
import studio.astroturf.quizzi.data.remote.websocket.model.PlayerDto
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizziApiService @Inject constructor(
    private val api: QuizziApi
) {
    suspend fun login(playerId: String): Result<PlayerDto> = runCatching {
        val request = LoginRequestDto(id = playerId)
        api.login(request)
    }.onFailure { e ->
        when (e) {
            is HttpException -> Timber.e(e, "Login failed for player: $playerId")
            else -> Timber.e(e, "Unexpected error during login")
        }
    }

    suspend fun createPlayer(name: String, avatarUrl: String): Result<PlayerDto> = runCatching {
        val request = CreatePlayerRequestDto(
            name = name,
            avatarUrl = avatarUrl
        )
        api.createPlayer(request)
    }.onFailure { e ->
        when (e) {
            is HttpException -> Timber.e(e, "Create player failed for name: $name")
            else -> Timber.e(e, "Unexpected error during player creation")
        }
    }

    suspend fun getRooms(): Result<RoomsDto> = runCatching {
        api.getRooms()
    }.onFailure { e ->
        when (e) {
            is HttpException -> Timber.e(e, "Failed to get rooms")
            else -> Timber.e(e, "Unexpected error while fetching rooms")
        }
    }
}