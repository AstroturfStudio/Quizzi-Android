package com.alicankorkmaz.quizzi.ui.screen.game

import com.alicankorkmaz.quizzi.domain.model.Question
import com.alicankorkmaz.quizzi.domain.model.RoomState
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerMessage

data class GameUiState(
    val currentQuestion: Question? = null,
    val timeRemaining: Long? = null,
    val roomState: RoomState? = null,
    val lastAnswerResult: ServerMessage.AnswerResult? = null,
    val showResult: Boolean = false,
    val error: String? = null,
    val score: Int = 0,
    val totalQuestions: Int = 10,
    val cursorPosition: Float = 0.5f,
    val winner: String? = null,
    val lastAnswer: ServerMessage.AnswerResult? = null,
    val hasAnswered: Boolean = false,
    val playerId: String? = null,
    val playerName: String? = null,
    val showRoundResult: Boolean = false,
    val correctAnswer: Int? = null,
    val winnerPlayerName: String? = null,
    val isWinner: Boolean = false,
    val roomId: String? = null,
    val playerIdToNameMap: Map<String, String> = emptyMap()
)