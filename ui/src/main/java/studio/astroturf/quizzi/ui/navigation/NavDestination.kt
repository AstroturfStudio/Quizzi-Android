package studio.astroturf.quizzi.ui.navigation

sealed class NavDestination(
    val route: String,
) {
    data object Onboarding : NavDestination("onboarding")

    data object Landing : NavDestination("landing")

    data object Search : NavDestination("search")

    data object Statistics : NavDestination("statistics")

    data object Profile : NavDestination("profile")

    data object Rooms : NavDestination("rooms")

    data object Create : NavDestination("create")

    object Game : NavDestination("game") {
        const val ROUTE_PATTERN = "game?roomId={roomId}"
        const val ARG_ROOM_ID = "roomId"

        fun createRoute(roomId: String? = null): String = if (roomId != null) "game?roomId=$roomId" else "game"
    }
}
