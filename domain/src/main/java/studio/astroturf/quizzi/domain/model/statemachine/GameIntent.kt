package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

sealed interface GameIntent {
    data class Lobby(val message: ServerMessage.RoomUpdate) : GameIntent
    data class Countdown(val message: ServerMessage.Countdown) : GameIntent
    data class StartRound(val message: ServerMessage.RoomUpdate) : GameIntent
    data class GameOver(val message: ServerMessage.GameOver) : GameIntent
    data class RoundEnd(val message: ServerMessage.RoundEnded) : GameIntent
    data class CloseRoom(val message: ServerMessage.RoomClosed) : GameIntent
    object ExitGame : GameIntent
}