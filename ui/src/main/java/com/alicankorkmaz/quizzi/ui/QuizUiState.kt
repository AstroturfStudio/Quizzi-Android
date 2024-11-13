package com.alicankorkmaz.quizzi.ui

import com.alicankorkmaz.quizzi.domain.model.ClientQuestion
import com.alicankorkmaz.quizzi.domain.model.RoomState
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerSocketMessage

data class QuizUiState(
    val currentQuestion: ClientQuestion? = null,
    val timeRemaining: Long? = null,
    val roomState: RoomState? = null,
    val lastAnswerResult: ServerSocketMessage.AnswerResult? = null,
    val showResult: Boolean = false,
    val error: String? = null,
    val score: Int = 0,
    val totalQuestions: Int = 10,
    val cursorPosition: Float = 0.5f,
    val winner: String? = null,
    val lastAnswer: ServerSocketMessage.AnswerResult? = null,
    val hasAnswered: Boolean = false,
    val playerId: String? = null,
    val playerName: String? = null,
    val showRoundResult: Boolean = false,
    val correctAnswer: Int? = null,
    val winnerPlayerName: String? = null,
    val isWinner: Boolean = false
) {
    val isGameOver: Boolean
        get() = roomState == RoomState.FINISHED
}