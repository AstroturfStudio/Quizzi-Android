package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

sealed interface GameIntent {
    data class RoomCreated(val message: ServerMessage.RoomCreated) : GameIntent
    data class RoomJoined(val message: ServerMessage.RoomJoined) : GameIntent
    data class Initialize(val message: ServerMessage.RoomUpdate) : GameIntent
    data class Playing(val roomUpdate: ServerMessage.RoomUpdate) : GameIntent
    data class RoundTimeUp(val message: ServerMessage.TimeUp) : GameIntent
    data class GameOver(val gameOver: ServerMessage.GameOver) : GameIntent
    data class RoundCompleted(val roundResult: ServerMessage.RoundResult) : GameIntent
    data class PlayerDisconnected(val message: ServerMessage.PlayerDisconnected) : GameIntent
    data class PlayerReconnected(val message: ServerMessage.PlayerReconnected) : GameIntent
    data class CloseRoom(val message: ServerMessage.RoomClosed) : GameIntent
    object ExitGame : GameIntent
}