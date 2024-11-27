package studio.astroturf.quizzi.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import studio.astroturf.quizzi.data.remote.rest.service.QuizziApiService
import studio.astroturf.quizzi.data.remote.websocket.model.PlayerDto
import studio.astroturf.quizzi.data.remote.websocket.service.QuizziWebSocketService
import studio.astroturf.quizzi.domain.model.GameRoom
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.repository.QuizRepository
import toDomain
import toDto
import javax.inject.Inject

class QuizRepositoryImpl
    @Inject
    constructor(
        private val quizziWebSocketService: QuizziWebSocketService,
        private val quizziApiService: QuizziApiService,
    ) : QuizRepository {
        private var currentPlayerDto: PlayerDto? = null

        override suspend fun login(playerId: String): Result<Player> =
            quizziApiService
                .login(playerId)
                .onSuccess {
                    currentPlayerDto = it
                }.map { it.toDomain() }

        override suspend fun createPlayer(
            name: String,
            avatarUrl: String,
        ): Result<Player> =
            quizziApiService
                .createPlayer(name, avatarUrl)
                .onSuccess {
                    currentPlayerDto = it
                }.map { it.toDomain() }

        override suspend fun getRooms(): Result<List<GameRoom>> =
            quizziApiService.getRooms().map { it.rooms.map { roomDto -> roomDto.toDomain() } }

        override fun connect() {
            quizziWebSocketService.connect(currentPlayerDto?.id)
        }

        override fun observeMessages(): Flow<ServerMessage> = quizziWebSocketService.observeMessages().map { it.toDomain() }

        override fun sendMessage(message: ClientMessage) {
            quizziWebSocketService.send(message.toDto())
    }

    override fun disconnect() {
        quizziWebSocketService.disconnect()
    }
} 
