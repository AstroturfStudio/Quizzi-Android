package studio.astroturf.quizzi.ui.screen.create.gametype

import studio.astroturf.quizzi.domain.model.GameType

data class GameTypeUiModel(
    val gameTypeName: String,
    val isSelected: Boolean = false,
) {
    companion object {
        fun from(gameType: GameType): GameTypeUiModel =
            GameTypeUiModel(
                gameTypeName = gameType.name,
            )
    }
}
