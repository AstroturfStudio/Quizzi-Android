package studio.astroturf.quizzi.ui.screen.game

import studio.astroturf.quizzi.domain.model.statemachine.Destination

sealed interface GameUiEvent {
    data class NavigateTo(val destination: Destination) : GameUiEvent
}