package com.alicankorkmaz.quizzi.domain.usecase

import com.alicankorkmaz.quizzi.domain.model.websocket.GameMessage
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow

class ObserveQuizUseCase(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<GameMessage> = repository.observeMessages()
} 