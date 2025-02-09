package studio.astroturf.quizzi.ui.screen.game.composables.lobby

import studio.astroturf.quizzi.domain.model.PlayerInRoom

data class LobbyPlayerUiModel(
    val player: PlayerInRoom,
    val isCreator: Boolean,
    val isReady: Boolean,
)
