package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.GameStatistics
import studio.astroturf.quizzi.domain.model.Option
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.Question

sealed interface GameState {
    object Idle : GameState

    data class Lobby(
        val roomId: String,
        val players: List<Player>,
    ) : GameState

    data class Starting(
        val timeRemaining: Int,
    ) : GameState

    data class RoundOn(
        val players: List<Player>,
        val currentQuestion: Question,
        val timeRemaining: Long,
        val cursorPosition: Float,
    ) : GameState

    data class Paused(
        val reason: String,
        val onlinePlayers: List<Player>,
        val disconnectedPlayers: List<Player>,
    ) : GameState

    data class EndOfRound(
        val cursorPosition: Float,
        val correctAnswer: Option,
        val winnerPlayer: Player?,
    ) : GameState

    data class GameOver(
        val winner: Player,
        val statistics: GameStatistics,
    ) : GameState
}
