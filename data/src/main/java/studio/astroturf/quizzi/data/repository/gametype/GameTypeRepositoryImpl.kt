package studio.astroturf.quizzi.data.repository.gametype

import studio.astroturf.quizzi.data.remote.rest.service.QuizziApiService
import studio.astroturf.quizzi.domain.model.GameType
import studio.astroturf.quizzi.domain.repository.GameTypeRepository
import studio.astroturf.quizzi.domain.result.QuizziResult
import studio.astroturf.quizzi.domain.result.map
import javax.inject.Inject

class GameTypeRepositoryImpl
    @Inject
    constructor(
        private val quizziApiService: QuizziApiService,
    ) : GameTypeRepository {
        override suspend fun getGameTypes(): QuizziResult<List<GameType>> =
            quizziApiService
                .getGameTypes()
                .map {
                    it.gameTypes.map { GameType(it) }
                }
    }
