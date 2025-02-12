package studio.astroturf.quizzi.ui.screen.game

import studio.astroturf.quizzi.domain.model.PlayerInRoom
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.ui.screen.game.composables.lobby.LobbyUiModel

sealed interface GameUiState {
    object Idle : GameUiState

    data class Lobby(
        val lobbyUiModel: LobbyUiModel,
    ) : GameUiState

    data class RoundOn(
        val player1: PlayerInRoom,
        val player2: PlayerInRoom?,
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
        val onlinePlayers: List<PlayerInRoom>,
    ) : GameUiState

    data class GameOver(
        val totalRoundCount: Int,
        val winnerName: String,
        val gameId: String,
    ) : GameUiState
}
