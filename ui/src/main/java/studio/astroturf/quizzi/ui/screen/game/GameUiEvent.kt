package studio.astroturf.quizzi.ui.screen.game

sealed interface GameUiEvent {
    data class ShowToast(
        val message: String,
    ) : GameUiEvent

    data class NavigateTo(
        val destination: Destination,
    ) : GameUiEvent {
        sealed interface Destination {
            object Rooms : Destination
        }
    }

    data class Error(
        val message: String,
    ) : GameUiEvent
}
