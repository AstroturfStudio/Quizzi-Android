package com.alicankorkmaz.quizzi.domain.repository

import com.alicankorkmaz.quizzi.domain.model.Player
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerSocketMessage
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun login(playerId: String): Result<Player>
    suspend fun createPlayer(name: String, avatarUrl: String): Result<Player>
    fun connect()
    fun sendAnswer(answer: Int)
    fun observeMessages(): Flow<ServerSocketMessage>
    fun disconnect()
    fun createRoom()
    fun joinRoom(roomId: String)
}