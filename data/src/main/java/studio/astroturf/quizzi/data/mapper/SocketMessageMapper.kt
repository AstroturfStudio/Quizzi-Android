import studio.astroturf.quizzi.data.remote.websocket.model.ClientSocketMessage
import studio.astroturf.quizzi.data.remote.websocket.model.ServerSocketMessage
import studio.astroturf.quizzi.domain.model.websocket.ClientMessage
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

fun ServerSocketMessage.toDomain(): ServerMessage =
    when (this) {
        is ServerSocketMessage.RoomCreated -> ServerMessage.RoomCreated(roomId = roomId)
        is ServerSocketMessage.JoinedRoom ->
            ServerMessage.RoomJoined(
                roomId = roomId,
                success = success,
            )

        is ServerSocketMessage.CountdownTimeUpdate -> ServerMessage.Countdown(remaining = remaining)
        is ServerSocketMessage.RoomUpdate ->
            ServerMessage.RoomUpdate(
                players = players.map { it.toDomain() },
                state = state,
                timeRemaining = timeRemaining,
                cursorPosition = cursorPosition,
                currentQuestion = currentQuestion?.toDomain(),
            )

        is ServerSocketMessage.AnswerResult ->
            ServerMessage.AnswerResult(
                playerId = playerId,
                answer = answer,
                correct = correct,
            )

        is ServerSocketMessage.Error -> ServerMessage.Error(message = message)
        is ServerSocketMessage.GameOver -> ServerMessage.GameOver(winnerPlayerId = winnerPlayerId)
        is ServerSocketMessage.PlayerDisconnected ->
            ServerMessage.PlayerDisconnected(
                playerId = playerId,
                playerName = playerName,
            )

        is ServerSocketMessage.PlayerReconnected -> ServerMessage.PlayerReconnected(playerId = playerId)
        is ServerSocketMessage.RoomClosed -> ServerMessage.RoomClosed(reason = reason)
        is ServerSocketMessage.RoundEnded ->
            ServerMessage.RoundEnded(
                cursorPosition = cursorPosition,
                correctAnswer = correctAnswer,
                winnerPlayerId = winnerPlayerId,
            )

        is ServerSocketMessage.RoundUpdate ->
            ServerMessage.RoundUpdate(
                roundNumber = roundNumber,
                timeRemaining = timeRemaining,
                currentQuestion = currentQuestion.toDomain(),
            )

        is ServerSocketMessage.TimeUp -> ServerMessage.TimeUp(correctAnswer = correctAnswer)
        is ServerSocketMessage.TimeUpdate -> ServerMessage.TimeUpdate(remaining = remaining)
    }

fun ClientMessage.toDto(): ClientSocketMessage =
    when (this) {
        is ClientMessage.CreateRoom -> ClientSocketMessage.CreateRoom
        is ClientMessage.JoinRoom -> ClientSocketMessage.JoinRoom(roomId = roomId)
        is ClientMessage.PlayerReady -> ClientSocketMessage.PlayerReady(playerId = playerId)
        is ClientMessage.PlayerAnswer -> ClientSocketMessage.PlayerAnswer(answer = answer)
        is ClientMessage.PlayerReconnected -> ClientSocketMessage.PlayerReconnected(playerId = playerId)
    }
