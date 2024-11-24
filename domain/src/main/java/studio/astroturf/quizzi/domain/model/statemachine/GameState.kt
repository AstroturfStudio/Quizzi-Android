package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.GameStatistics
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.Question

sealed interface GameState {
    object Idle : GameState

    data class Lobby(
        val creator: Player,
        val invitee: Player?,
        val isReady: Map<String, Boolean>
    ) : GameState

    data class Initializing(val timeRemaining: Int) : GameState

    data class RoundActive(
        val players: List<Player>,
        val currentQuestion: Question,
        val timeRemaining: Long?,
        val cursorPosition: Float,
        val currentAnswerIndex: Int?
    ) : GameState

    data class RoundEnd(
        val correctAnswer: String,
        val roundWinner: Player
    ) : GameState

    data class GameOver(
        val winnerPlayerId: String,
        val statistics: GameStatistics
    ) : GameState
}