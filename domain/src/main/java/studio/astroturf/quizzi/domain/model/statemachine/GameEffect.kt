package studio.astroturf.quizzi.domain.model.statemachine

sealed interface GameEffect {
    data class ShowToast(val message: String) : GameEffect
    data class NavigateTo(val destination: NavDestination) : GameEffect
}

sealed interface NavDestination {
    object Rooms : NavDestination
}