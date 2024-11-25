package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

sealed interface GameEffect {
    data class ShowToast(val message: String) : GameEffect
    data class NavigateTo(val destination: Destination) : GameEffect
    data class ShowTimeRemaining(val timeRemaining: Long) : GameEffect
    data class ShowError(val message: String) : GameEffect
    data class ReceiveAnswerResult(val answerResult: ServerMessage.AnswerResult) : GameEffect
}

sealed interface Destination {
    object Rooms : Destination
}