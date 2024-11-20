import studio.astroturf.quizzi.data.remote.websocket.model.ClientSocketMessage
import studio.astroturf.quizzi.data.remote.websocket.model.ServerSocketMessage
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

fun ServerSocketMessage.toDomain(): ServerMessage = when (this) {
    is ServerSocketMessage.RoomCreated -> ServerMessage.RoomCreated(roomId)
    is ServerSocketMessage.JoinedRoom -> ServerMessage.RoomJoined(roomId, success)
    is ServerSocketMessage.RoomUpdate -> ServerMessage.RoomUpdate(
        players = players.map { it.toDomain() },
        state = state,
        cursorPosition = cursorPosition,
        timeRemaining = timeRemaining,
        currentQuestion = currentQuestion?.toDomain()
    )

    is ServerSocketMessage.AnswerResult -> ServerMessage.AnswerResult(playerId, answer, correct)
    is ServerSocketMessage.Error -> ServerMessage.Error(message)
    is ServerSocketMessage.GameOver -> ServerMessage.GameOver(winnerPlayerId)
    is ServerSocketMessage.PlayerDisconnected -> ServerMessage.PlayerDisconnected(
        playerId,
        playerName
    )

    is ServerSocketMessage.PlayerReconnected -> ServerMessage.PlayerReconnected(playerId)
    is ServerSocketMessage.RoomClosed -> ServerMessage.RoomClosed(reason)
    is ServerSocketMessage.RoundResult -> ServerMessage.RoundResult(correctAnswer, winnerPlayerId)
    is ServerSocketMessage.TimeUp -> ServerMessage.TimeUp(correctAnswer)
    is ServerSocketMessage.TimeUpdate -> ServerMessage.TimeUpdate(remaining)
}

fun ClientMessage.toDto(): ClientSocketMessage = when (this) {
    is ClientMessage.CreateRoom -> ClientSocketMessage.CreateRoom
    is ClientMessage.JoinRoom -> ClientSocketMessage.JoinRoom(roomId)
    is ClientMessage.PlayerReady -> ClientSocketMessage.PlayerReady(playerId)
    is ClientMessage.PlayerAnswer -> ClientSocketMessage.PlayerAnswer(answer)
    is ClientMessage.PlayerReconnected -> ClientSocketMessage.PlayerReconnected(playerId)
} 