package studio.astroturf.quizzi.domain.usecase

import kotlinx.coroutines.flow.Flow
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.repository.QuizRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<ServerMessage> =
        repository.observeMessages()
} 