package studio.astroturf.quizzi.ui.screen.create.gametype

import studio.astroturf.quizzi.domain.model.GameType

data class GameTypeUiModel(
    val gameType: GameType,
    val isSelected: Boolean = false,
) {
    companion object {
        fun from(gameType: GameType): GameTypeUiModel =
            GameTypeUiModel(
                gameType = gameType,
            )
    }
}
