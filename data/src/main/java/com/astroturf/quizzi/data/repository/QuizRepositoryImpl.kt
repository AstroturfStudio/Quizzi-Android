package com.astroturf.quizzi.data.repository

import com.astroturf.quizzi.data.remote.WebSocketService
import com.astroturf.quizzi.data.remote.api.QuizziApi
import com.astroturf.quizzi.data.remote.model.CreatePlayerRequestDto
import com.astroturf.quizzi.data.remote.model.LoginRequestDto
import com.astroturf.quizzi.data.remote.model.PlayerDto
import com.astroturf.quizzi.domain.model.GameRoom
import com.astroturf.quizzi.domain.model.Player
import com.astroturf.quizzi.domain.model.websocket.ClientMessage
import com.astroturf.quizzi.domain.model.websocket.ServerMessage
import com.astroturf.quizzi.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import toDomain
import toDto
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val webSocketService: WebSocketService,
    private val api: QuizziApi
) : QuizRepository {

    private var currentPlayerDto: PlayerDto? = null

    override suspend fun login(playerId: String): Result<Player> =
        runCatching {
            api.login(
                LoginRequestDto(
                    id = playerId
                )
            ).also {
                currentPlayerDto = it
            }.toDomain()
        }

    override suspend fun createPlayer(name: String, avatarUrl: String): Result<Player> {
        return runCatching {
            api.createPlayer(
                CreatePlayerRequestDto(
                    name = name,
                    avatarUrl = avatarUrl
                )
            ).also {
                currentPlayerDto = it
            }.toDomain()
        }
    }

    override suspend fun getRooms(): Result<List<GameRoom>> {
        return runCatching {
            api.getRooms().rooms.map { it.toDomain() }
        }
    }

    override fun connect() {
        webSocketService.connect(playerId = currentPlayerDto?.id)
    }

    override fun observeMessages(): Flow<ServerMessage> {
        return webSocketService.observeMessages().map { it.toDomain() }
    }

    override fun sendMessage(message: ClientMessage) {
        webSocketService.send(message.toDto())
    }

    override fun disconnect() {
        webSocketService.disconnect()
    }
} 