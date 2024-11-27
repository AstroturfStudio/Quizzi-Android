sealed class NavDestination(
    val route: String,
) {
    data object Landing : NavDestination("landing")

    data object Rooms : NavDestination("rooms")

    object Game : NavDestination("game") {
        const val ROUTE_PATTERN = "game?roomId={roomId}"
        const val ARG_ROOM_ID = "roomId"

        fun createRoute(roomId: String? = null): String = if (roomId != null) "game?roomId=$roomId" else "game"
    }
}
