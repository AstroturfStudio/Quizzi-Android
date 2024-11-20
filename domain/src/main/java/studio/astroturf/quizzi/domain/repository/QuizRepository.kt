package studio.astroturf.quizzi.domain.repository

import kotlinx.coroutines.flow.Flow
import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

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