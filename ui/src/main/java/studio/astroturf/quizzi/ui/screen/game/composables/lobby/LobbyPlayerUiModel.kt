package studio.astroturf.quizzi.ui.screen.game.composables.lobby

import studio.astroturf.quizzi.domain.model.Player

data class LobbyPlayerUiModel(
    val player: Player,
    val isCreator: Boolean,
    val isReady: Boolean,
)
