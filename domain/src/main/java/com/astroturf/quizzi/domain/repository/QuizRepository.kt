package com.astroturf.quizzi.domain.repository

import com.astroturf.quizzi.domain.model.GameRoom
import com.astroturf.quizzi.domain.model.Player
import com.astroturf.quizzi.domain.model.websocket.ClientMessage
import com.astroturf.quizzi.domain.model.websocket.ServerMessage
import kotlinx.coroutines.flow.Flow

interface QuizRepository {

    suspend fun login(playerId: String): Result<Player>

    suspend fun createPlayer(
        name: String,
        avatarUrl: String
    ): Result<Player>

    suspend fun getRooms(): Result<List<GameRoom>>

    fun connect()

    fun observeMessages(): Flow<ServerMessage>

    fun sendMessage(message: ClientMessage)

    fun disconnect()
}