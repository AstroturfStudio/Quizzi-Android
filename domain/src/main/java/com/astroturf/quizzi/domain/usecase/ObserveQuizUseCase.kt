package com.astroturf.quizzi.domain.usecase

import com.astroturf.quizzi.domain.model.websocket.ServerMessage
import com.astroturf.quizzi.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<ServerMessage> =
        repository.observeMessages()
} 