package com.astroturf.quizzi.domain.model.websocket

sealed class ClientMessage {
    data object CreateRoom : ClientMessage()

    data class JoinRoom(
        val roomId: String
    ) : ClientMessage()

    data class PlayerReady(
        val playerId: String
    ) : ClientMessage()

    data class PlayerAnswer(
        val answer: Int
    ) : ClientMessage()

    data class PlayerReconnected(
        val playerId: String
    ) : ClientMessage()
} 