package com.alicankorkmaz.quizzi.data.repository

import com.alicankorkmaz.quizzi.domain.model.websocket.ClientSocketMessage
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerSocketMessage
import com.alicankorkmaz.quizzi.data.remote.WebSocketService
import com.alicankorkmaz.quizzi.data.remote.api.CreatePlayerRequest
import com.alicankorkmaz.quizzi.data.remote.api.LoginRequest
import com.alicankorkmaz.quizzi.data.remote.api.QuizApi
import com.alicankorkmaz.quizzi.domain.model.Player
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val webSocketService: WebSocketService,
    private val api: QuizApi
) : QuizRepository {

    private var currentPlayer: Player? = null

    override suspend fun login(playerId: String): Result<Player> =
        runCatching {
            api.login(
                LoginRequest(
                    id = playerId
                )
            ).also { player ->
                currentPlayer = player
                webSocketService.connect(player.id)
            }
        }

    override suspend fun createPlayer(name: String, avatarUrl: String): Result<Player> {
        return runCatching {
            api.createPlayer(
                CreatePlayerRequest(
                    name = name,
                    avatarUrl = avatarUrl
                )
            ).also { player ->
                currentPlayer = player
                webSocketService.connect(player.id)
            }
        }
    }

    override fun connect() {
        webSocketService.connect()
    }

    override fun createRoom() {
        webSocketService.send(ClientSocketMessage.CreateRoom)
    }

    override fun joinRoom(roomId: String) {
        currentPlayer?.let { player ->
            webSocketService.send(ClientSocketMessage.JoinRoom(roomId))
        }
    }

    override fun sendAnswer(answer: Int) {
        webSocketService.send(ClientSocketMessage.PlayerAnswer(answer))
    }

    override fun observeMessages(): Flow<ServerSocketMessage> {
        return webSocketService.observeMessages()
    }

    override fun disconnect() {
        webSocketService.disconnect()
    }
} 