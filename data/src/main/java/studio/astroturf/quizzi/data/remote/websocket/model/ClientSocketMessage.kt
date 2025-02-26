package studio.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class ClientSocketMessage {
    @Serializable
    @SerialName("CreateRoom")
    data class CreateRoom(
        val roomName: String,
        val categoryId: Int,
        val gameType: String,
    ) : ClientSocketMessage()

    @Serializable
    @SerialName("JoinRoom")
    data class JoinRoom(
        val roomId: String,
    ) : ClientSocketMessage()

    @Serializable
    @SerialName("RejoinRoom")
    data class RejoinRoom(
        val roomId: String,
    ) : ClientSocketMessage()

    @Serializable
    @SerialName("PlayerReady")
    data object PlayerReady : ClientSocketMessage()

    @Serializable
    @SerialName("PlayerAnswer")
    data class PlayerAnswer(
        val answer: Int,
    ) : ClientSocketMessage()

    @Serializable
    @SerialName("PlayerReconnected")
    data class PlayerReconnected(
        val playerId: String,
    ) : ClientSocketMessage()
}
