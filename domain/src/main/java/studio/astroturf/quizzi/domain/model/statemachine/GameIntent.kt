package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

sealed interface GameIntent {
    data class CreateLobby(val creator: Player) : GameIntent
    data class UpdateRoom(val roomUpdate: ServerMessage.RoomUpdate) : GameIntent
    data class SelectAnswer(val answerIndex: Int) : GameIntent
    data class TimeUpdate(val update: ServerMessage.TimeUpdate) : GameIntent
    data class Countdown(val timeRemaining: Int) : GameIntent
    data class GameOver(val gameOver: ServerMessage.GameOver) : GameIntent
    data class AnswerResult(val answerResult: ServerMessage.AnswerResult) : GameIntent
    data class RoundResult(val roundResult: ServerMessage.RoundResult) : GameIntent
    object ReturnToRooms : GameIntent
}