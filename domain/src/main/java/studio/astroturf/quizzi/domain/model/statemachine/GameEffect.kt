package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

sealed interface GameEffect {
    data class ShowToast(val message: String) : GameEffect
    data class NavigateTo(val destination: Destination) : GameEffect
    data class ShowTimeRemaining(val timeRemaining: Long) : GameEffect
    data class ShowError(val message: String) : GameEffect
    data class RoundUpdate(val message: ServerMessage.RoundUpdate) : GameEffect
    data class RoundTimeUp(val message: ServerMessage.TimeUp) : GameEffect
    data class ReceiveAnswerResult(val answerResult: ServerMessage.AnswerResult) : GameEffect
    data class PlayerDisconnected(val message: ServerMessage.PlayerDisconnected) : GameEffect
    data class PlayerReconnected(val message: ServerMessage.PlayerReconnected) : GameEffect
    data class RoomCreated(val message: ServerMessage.RoomCreated) : GameEffect
    data class RoomJoined(val message: ServerMessage.RoomJoined) : GameEffect
}

sealed interface Destination {
    object Rooms : Destination
}