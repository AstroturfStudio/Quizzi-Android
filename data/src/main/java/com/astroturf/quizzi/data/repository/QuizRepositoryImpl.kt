package com.astroturf.quizzi.data.repository

import com.astroturf.quizzi.data.remote.rest.service.QuizziApiService
import com.astroturf.quizzi.data.remote.websocket.model.PlayerDto
import com.astroturf.quizzi.data.remote.websocket.service.QuizziWebSocketService
import com.astroturf.quizzi.domain.model.GameRoom
import com.astroturf.quizzi.domain.model.Player
import com.astroturf.quizzi.domain.model.websocket.ClientMessage
import com.astroturf.quizzi.domain.model.websocket.ServerMessage
import com.astroturf.quizzi.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import toDomain
import toDto

class QuizRepositoryImpl(
    private val quizziWebSocketService: QuizziWebSocketService,
    private val quizziApiService: QuizziApiService
) : QuizRepository {

    private var currentPlayerDto: PlayerDto? = null

    override suspend fun login(playerId: String): Result<Player> =
        quizziApiService.login(playerId).onSuccess {
            currentPlayerDto = it
        }.map { it.toDomain() }

    override suspend fun createPlayer(name: String, avatarUrl: String): Result<Player> =
        quizziApiService.createPlayer(name, avatarUrl).onSuccess {
            currentPlayerDto = it
        }.map { it.toDomain() }

    override suspend fun getRooms(): Result<List<GameRoom>> =
        quizziApiService.getRooms().map { it.rooms.map { roomDto -> roomDto.toDomain() } }

    override fun connect() {
        quizziWebSocketService.connect(currentPlayerDto?.id)
    }

    override fun observeMessages(): Flow<ServerMessage> {
        return quizziWebSocketService.observeMessages().map { it.toDomain() }
    }

    override fun sendMessage(message: ClientMessage) {
        quizziWebSocketService.send(message.toDto())
    }

    override fun disconnect() {
        quizziWebSocketService.disconnect()
    }
} 