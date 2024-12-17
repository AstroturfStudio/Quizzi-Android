package studio.astroturf.quizzi.data.repository.rooms

import studio.astroturf.quizzi.data.remote.rest.service.QuizziApiService
import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.repository.RoomsRepository
import studio.astroturf.quizzi.domain.result.QuizziResult
import studio.astroturf.quizzi.domain.result.map
import javax.inject.Inject

class RoomsRepositoryImpl
    @Inject
    constructor(
        private val quizziApiService: QuizziApiService,
    ) : RoomsRepository {
        override suspend fun getRooms(): QuizziResult<List<GameRoom>> =
            quizziApiService
                .getRooms()
                .map { it.rooms.map { roomDto -> roomDto.toDomain() } }
    }
