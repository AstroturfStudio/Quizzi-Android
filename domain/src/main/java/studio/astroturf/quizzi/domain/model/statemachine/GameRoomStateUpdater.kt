package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

sealed interface GameRoomStateUpdater {
    data class RoundStarted(
        val message: ServerMessage.RoundStarted,
    ) : GameRoomStateUpdater

    data class RoundTimeUp(
        val message: ServerMessage.TimeUp,
    ) : GameRoomStateUpdater

    data class ReceiveAnswerResult(
        val answerResult: ServerMessage.AnswerResult,
    ) : GameRoomStateUpdater

    data class PlayerDisconnected(
        val message: ServerMessage.PlayerDisconnected,
    ) : GameRoomStateUpdater

    data class PlayerReconnected(
        val message: ServerMessage.PlayerReconnected,
    ) : GameRoomStateUpdater

    data class RoomCreated(
        val message: ServerMessage.RoomCreated,
    ) : GameRoomStateUpdater

    data class RoomJoined(
        val message: ServerMessage.JoinedRoom,
    ) : GameRoomStateUpdater

    data class RoundEnd(
        val message: ServerMessage.RoundEnded,
    ) : GameRoomStateUpdater

    data class Countdown(
        val message: ServerMessage.CountdownTimeUpdate,
    ) : GameRoomStateUpdater

    data class GameRoomOver(
        val message: ServerMessage.GameOver,
    ) : GameRoomStateUpdater

    data class CloseRoom(
        val message: ServerMessage.RoomClosed,
    ) : GameRoomStateUpdater

    object ExitGameRoom : GameRoomStateUpdater

    data class RoundTimeUpdate(
        val message: ServerMessage.TimeUpdate,
    ) : GameRoomStateUpdater

    data class Error(
        val message: ServerMessage.Error,
    ) : GameRoomStateUpdater
}
