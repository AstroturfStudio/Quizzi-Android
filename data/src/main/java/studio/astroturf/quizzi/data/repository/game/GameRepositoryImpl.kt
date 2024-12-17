package studio.astroturf.quizzi.data.repository.game

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import studio.astroturf.quizzi.data.exceptionhandling.mapToQuizziException
import studio.astroturf.quizzi.data.remote.websocket.service.QuizziWebSocketService
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.network.GameConnectionStatus
import studio.astroturf.quizzi.domain.repository.GameRepository
import studio.astroturf.quizzi.domain.result.QuizziResult
import toDomain
import toDto
import javax.inject.Inject

class GameRepositoryImpl
    @Inject
    constructor(
        private val quizziWebSocketService: QuizziWebSocketService,
    ) : GameRepository {
        override fun observeConnectionStatus(): StateFlow<GameConnectionStatus> = quizziWebSocketService.connectionStatus

        override fun connect(currentPlayerId: String?): QuizziResult<Unit> =
            try {
                quizziWebSocketService.connect(currentPlayerId)
                QuizziResult.success(Unit)
            } catch (e: Exception) {
                QuizziResult.failure(e.mapToQuizziException())
            }

        override fun observeMessages(): Flow<ServerMessage> = quizziWebSocketService.observeMessages().map { it.toDomain() }

        override fun sendMessage(message: ClientMessage) {
            quizziWebSocketService.send(message.toDto())
        }

        override fun disconnect() {
            quizziWebSocketService.disconnect()
        }
    }
