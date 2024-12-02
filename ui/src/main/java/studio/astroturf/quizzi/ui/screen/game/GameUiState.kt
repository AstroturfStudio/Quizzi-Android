package studio.astroturf.quizzi.ui.screen.game

import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.ui.screen.game.composables.roundend.RoundWinner

sealed interface GameUiState {
    object Idle : GameUiState

    data class Lobby(
        val roomName: String,
        val creator: Player,
        val challenger: Player?,
        val countdown: CountdownTimer?,
    ) : GameUiState {
        data class CountdownTimer(
            val timeRemainingInSeconds: Int,
        )
    }

    data class RoundOn(
        val player1: Player,
        val player2: Player,
        val gameBarPercentage: Float,
        val question: Question,
        val timeRemainingInSeconds: Int,
        val selectedAnswerId: Int? = null,
        val playerRoundResult: PlayerRoundResult? = null,
    ) : GameUiState {
        data class PlayerRoundResult(
            val answerId: Int,
            val isCorrect: Boolean,
        )
    }

    data class Paused(
        val reason: String,
        val onlinePlayers: List<Player>,
    ) : GameUiState

    data class RoundEnd(
        val roundNo: Int,
        val roundWinner: RoundWinner,
        val correctAnswerValue: String,
        val newCursorPosition: Float,
    ) : GameUiState

    data class GameOver(
        val totalRoundCount: Int?,
        val winner: Player?,
    ) : GameUiState
}
