package com.alicankorkmaz.flagquiz.domain.usecase

import com.alicankorkmaz.flagquiz.domain.model.websocket.GameMessage
import com.alicankorkmaz.flagquiz.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow

class ObserveQuizUseCase(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<GameMessage> = repository.observeMessages()
} 