package com.alicankorkmaz.quizzi.domain.model.websocket

import com.alicankorkmaz.quizzi.domain.model.ClientQuestion
import com.alicankorkmaz.quizzi.domain.model.Player
import com.alicankorkmaz.quizzi.domain.model.RoomState

sealed class ServerMessage {
    data class RoomCreated(
        val roomId: String
    ) : ServerMessage()

    data class RoomJoined(
        val roomId: String,
        val success: Boolean
    ) : ServerMessage()

    data class RoomUpdate(
        val players: List<Player>,
        val state: RoomState,
        val cursorPosition: Float,
        val timeRemaining: Long?,
        val currentQuestion: ClientQuestion?
    ) : ServerMessage()

    data class TimeUpdate(
        val remaining: Long
    ) : ServerMessage()

    data class TimeUp(
        val correctAnswer: Int
    ) : ServerMessage()

    data class AnswerResult(
        val playerId: String,
        val answer: Int,
        val correct: Boolean
    ) : ServerMessage()

    data class RoundResult(
        val correctAnswer: Int,
        val winnerPlayerId: String?
    ) : ServerMessage()

    data class GameOver(
        val winnerPlayerId: String
    ) : ServerMessage()

    data class PlayerDisconnected(
        val playerId: String,
        val playerName: String
    ) : ServerMessage()

    data class PlayerReconnected(
        val playerId: String
    ) : ServerMessage()

    data class RoomClosed(
        val reason: String
    ) : ServerMessage()

    data class Error(
        val message: String
    ) : ServerMessage()
} 