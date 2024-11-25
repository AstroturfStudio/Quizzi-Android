package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.GameStatistics
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.Question

sealed interface GameState {
    object Idle : GameState

    data class Lobby(
        val roomId: String,
    ) : GameState

    data class Starting(val timeRemaining: Int) : GameState

    data class RoundOn(
        val players: List<Player>,
        val currentQuestion: Question,
        val timeRemaining: Long?,
        val cursorPosition: Float,
    ) : GameState

    data class Paused(
        val onlinePlayers: List<Player>,
        val disconnectedPlayers: List<Player>
    ) : GameState

    data class EndOfRound(
        val cursorPosition: Float,
        val correctAnswer: Int,
        val winnerPlayerId: String?
    ) : GameState

    data class GameOver(
        val winnerPlayerId: String,
        val statistics: GameStatistics
    ) : GameState
}