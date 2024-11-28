package studio.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import studio.astroturf.quizzi.domain.model.RoomState

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class ServerSocketMessage {
    @Serializable
    @SerialName("RoomCreated")
    data class RoomCreated(
        val roomId: String,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("JoinedRoom")
    data class JoinedRoom(
        val roomId: String,
        val success: Boolean,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("CountdownTimeUpdate")
    data class CountdownTimeUpdate(
        val remaining: Long,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("RoomUpdate")
    data class RoomUpdate(
        val players: List<PlayerDto>,
        val state: RoomState,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("TimeUpdate")
    data class TimeUpdate(
        val remaining: Long,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("TimeUp")
    data class TimeUp(
        val correctAnswer: Int,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("AnswerResult")
    data class AnswerResult(
        val playerId: String,
        val answer: Int,
        val correct: Boolean,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("RoundStarted")
    data class RoundStarted(
        val roundNumber: Int,
        val timeRemaining: Long,
        val currentQuestion: QuestionDto,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("RoundEnded")
    data class RoundEnded(
        val cursorPosition: Float,
        val correctAnswer: Int,
        val winnerPlayerId: String?,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("GameOver")
    data class GameOver(
        val winnerPlayerId: String,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("PlayerDisconnected")
    data class PlayerDisconnected(
        val playerId: String,
        val playerName: String,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("PlayerReconnected")
    data class PlayerReconnected(
        val playerId: String,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("RoomClosed")
    data class RoomClosed(
        val reason: String,
    ) : ServerSocketMessage()

    @Serializable
    @SerialName("Error")
    data class Error(
        val message: String,
    ) : ServerSocketMessage()
}
