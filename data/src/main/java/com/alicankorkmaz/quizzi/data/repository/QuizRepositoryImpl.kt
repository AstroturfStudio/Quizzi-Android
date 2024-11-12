package com.alicankorkmaz.quizzi.data.repository

import com.alicankorkmaz.quizzi.data.remote.WebSocketService
import com.alicankorkmaz.quizzi.domain.model.websocket.GameMessage
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow

class QuizRepositoryImpl(
    private val webSocketService: WebSocketService
) : QuizRepository {

    override fun connect() {
        webSocketService.connect()
    }

    override fun createRoom(playerName: String) {
        val message = GameMessage.CreateRoom(playerName = playerName)
        webSocketService.send(message)
    }

    override fun joinRoom(roomId: String, playerName: String) {
        val message = GameMessage.JoinRoom(roomId = roomId, playerName = playerName)
        webSocketService.send(message)
    }

    override fun sendAnswer(answer: String) {
        val message = GameMessage.PlayerAnswer(answer = answer)
        webSocketService.send(message)
    }

    override fun observeMessages(): Flow<GameMessage> {
        return webSocketService.observeMessages()
    }

    override fun disconnect() {
        webSocketService.disconnect()
    }
} 