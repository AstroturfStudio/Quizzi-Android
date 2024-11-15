package com.alicankorkmaz.quizzi.domain.usecase

import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<ServerMessage> =
        repository.observeMessages()
} 