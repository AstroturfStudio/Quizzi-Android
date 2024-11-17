package com.astroturf.quizzi.data.remote.websocket.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class ClientSocketMessage {
    @Serializable
    @SerialName("CreateRoom")
    data object CreateRoom : ClientSocketMessage()

    @Serializable
    @SerialName("JoinRoom")
    data class JoinRoom(
        val roomId: String
    ) : ClientSocketMessage()

    @Serializable
    @SerialName("PlayerReady")
    data class PlayerReady(
        val playerId: String
    ) : ClientSocketMessage()

    @Serializable
    @SerialName("PlayerAnswer")
    data class PlayerAnswer(
        val answer: Int
    ) : ClientSocketMessage()

    @Serializable
    @SerialName("PlayerReconnected")
    data class PlayerReconnected(
        val playerId: String
    ) : ClientSocketMessage()
} 