package studio.astroturf.quizzi.ui.screen.create.gametype

import androidx.annotation.StringRes
import studio.astroturf.quizzi.domain.model.GameType

data class GameTypeUiModel(
    val gameType: GameType,
    @StringRes val gameTypeNameResId: Int,
    val isSelected: Boolean = false,
) {
    companion object {
        fun from(gameType: GameType): GameTypeUiModel =
            GameTypeUiModel(
                gameType = gameType,
                gameTypeNameResId = gameType.getGameTypeNameResId(),
            )
    }
}
