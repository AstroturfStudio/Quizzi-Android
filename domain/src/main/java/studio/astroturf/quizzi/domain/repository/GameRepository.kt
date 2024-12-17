package studio.astroturf.quizzi.domain.repository

import kotlinx.coroutines.flow.Flow
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage
import studio.astroturf.quizzi.domain.result.QuizziResult

interface GameRepository {
    fun connect(): QuizziResult<Unit>

    fun observeMessages(): Flow<ServerMessage>

    fun sendMessage(message: ClientMessage)

    fun disconnect()
}
