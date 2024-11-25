import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import studio.astroturf.quizzi.ui.screen.game.GameScreen2
import studio.astroturf.quizzi.ui.screen.landing.LandingScreen
import studio.astroturf.quizzi.ui.screen.rooms.RoomIntent

@Composable
fun QuizziNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavDestination.Landing.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavDestination.Landing.route) {
            LandingScreen(
                onNavigateToRooms = {
                    navController.navigate(NavDestination.Rooms.route) {
                        popUpTo(NavDestination.Landing.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavDestination.Rooms.route) {
            RoomsScreen(
                onNavigateToRoom = { roomIntent ->
                    when (roomIntent) {
                        RoomIntent.CreateRoom -> {
                            navController.navigate(NavDestination.Game.createRoute())
                        }

                        is RoomIntent.JoinRoom -> {
                            navController.navigate(NavDestination.Game.createRoute(roomId = roomIntent.roomId))
                        }
                    }

                }
            )
        }

        composable(
            route = NavDestination.Game.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(NavDestination.Game.ARG_ROOM_ID) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            GameScreen2(
                onNavigateToRooms = {
                    navController.navigate(NavDestination.Rooms.route) {
                        popUpTo(NavDestination.Rooms.route) { inclusive = true }
                    }
                },
                onShowError = {},
                onShowToast = { }
            )
        }
    }
} 