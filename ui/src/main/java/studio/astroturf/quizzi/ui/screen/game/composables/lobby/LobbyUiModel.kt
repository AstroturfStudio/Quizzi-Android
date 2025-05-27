package studio.astroturf.quizzi.ui.screen.game.composables.lobby

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class LobbyUiModel(
    val roomTitle: String,
    @StringRes val categoryNameResId: Int,
    @StringRes val gameTypeResId: Int,
    @StringRes val gameTypeDescriptionResId: Int,
    @DrawableRes val gameTypeIconResId: Int,
    val players: List<LobbyPlayerUiModel>,
    val currentUserReady: Boolean,
    val countdown: Int?,
)
