package com.alicankorkmaz.flagquiz.ui

import com.alicankorkmaz.flagquiz.domain.model.ClientQuestion
import com.alicankorkmaz.flagquiz.domain.model.GameState
import com.alicankorkmaz.flagquiz.domain.model.websocket.GameMessage

data class QuizUiState(
    val currentQuestion: ClientQuestion? = null,
    val timeRemaining: Long? = null,
    val gameState: GameState? = null,
    val lastAnswerResult: GameMessage.AnswerResult? = null,
    val showResult: Boolean = false,
    val error: String? = null,
    val score: Int = 0,
    val totalQuestions: Int = 10,
    val cursorPosition: Float = 0.5f,
    val winner: String? = null,
    val lastAnswer: GameMessage.AnswerResult? = null,
    val hasAnswered: Boolean = false
) {
    val isGameOver: Boolean
        get() = gameState == GameState.FINISHED
}