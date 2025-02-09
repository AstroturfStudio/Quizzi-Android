package studio.astroturf.quizzi.ui.screen.game.composables.lobby

data class LobbyUiModel(
    val roomTitle: String,
    val categoryName: String,
    val gameType: String,
    val players: List<LobbyPlayerUiModel>,
    val currentUserReady: Boolean,
    val countdown: Int?,
)
