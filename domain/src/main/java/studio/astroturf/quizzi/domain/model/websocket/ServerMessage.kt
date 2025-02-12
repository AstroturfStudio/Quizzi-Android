package studio.astroturf.quizzi.domain.model.websocket

import studio.astroturf.quizzi.domain.model.PlayerInRoom
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.domain.model.RoomState

sealed class ServerMessage {
    data class RoomCreated(
        val roomId: String,
    ) : ServerMessage()

    data class JoinedRoom(
        val roomId: String,
        val success: Boolean,
    ) : ServerMessage()

    data class RejoinedRoom(
        val roomId: String,
        val playerId: String,
        val success: Boolean,
    ) : ServerMessage()

    data class RoomUpdate(
        val players: List<PlayerInRoom>,
        val state: RoomState,
    ) : ServerMessage()

    data class CountdownTimeUpdate(
        val remaining: Long,
    ) : ServerMessage()

    data class RoundStarted(
        val roundNumber: Int,
        val timeRemaining: Long,
        val currentQuestion: Question,
    ) : ServerMessage()

    data class TimeUpdate(
        val remaining: Long,
    ) : ServerMessage()

    data class AnswerResult(
        val playerId: String,
        val answer: Int,
        val correct: Boolean,
    ) : ServerMessage()

    data class CursorRoundEnded(
        val cursorPosition: Float,
        val correctAnswer: Int,
        val winnerPlayerId: String?,
    ) : ServerMessage()

    data class StandardRoundEnded(
        val correctAnswer: Int,
        val winnerPlayerId: String?,
    ) : ServerMessage()

    data class TimeUp(
        val correctAnswer: Int,
    ) : ServerMessage()

    data class GameOver(
        val winnerPlayerId: String?,
    ) : ServerMessage()

    data class Error(
        val message: String,
    ) : ServerMessage()

    data class PlayerDisconnected(
        val playerId: String,
        val playerName: String,
    ) : ServerMessage()

    data class PlayerReconnected(
        val playerId: String,
    ) : ServerMessage()

    data class RoomClosed(
        val reason: String,
    ) : ServerMessage()
}
