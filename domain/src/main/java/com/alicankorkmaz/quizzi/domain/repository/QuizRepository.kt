package com.alicankorkmaz.quizzi.domain.repository

import com.alicankorkmaz.quizzi.domain.model.websocket.GameMessage
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun connect()
    fun sendAnswer(answer: String)
    fun observeMessages(): Flow<GameMessage>
    fun disconnect()
    fun createRoom(playerName: String)
    fun joinRoom(roomId: String, playerName: String)
}