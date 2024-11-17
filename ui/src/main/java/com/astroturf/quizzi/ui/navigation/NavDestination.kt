sealed class NavDestination(val route: String) {
    data object Landing : NavDestination("landing")
    data object Rooms : NavDestination("rooms")
    
    data class Game(val roomId: String? = null) : NavDestination("game/{roomId}") {
        fun createRoute(roomId: String) = "game/$roomId"
        
        companion object {
            const val ARG_ROOM_ID = "roomId"
        }
    }
} 