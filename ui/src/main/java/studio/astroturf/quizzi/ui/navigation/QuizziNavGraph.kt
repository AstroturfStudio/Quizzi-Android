package studio.astroturf.quizzi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import studio.astroturf.quizzi.ui.screen.create.CreateScreen
import studio.astroturf.quizzi.ui.screen.game.GameScreen
import studio.astroturf.quizzi.ui.screen.landing.LandingScreen
import studio.astroturf.quizzi.ui.screen.onboarding.OnboardingScreen
import studio.astroturf.quizzi.ui.screen.profile.ProfileScreen
import studio.astroturf.quizzi.ui.screen.rooms.RoomIntent
import studio.astroturf.quizzi.ui.screen.rooms.RoomsScreen
import studio.astroturf.quizzi.ui.screen.search.SearchScreen
import studio.astroturf.quizzi.ui.screen.statistics.StatisticsScreen

@Composable
fun QuizziNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavDestination.Onboarding.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(NavDestination.Onboarding.route) {
            OnboardingScreen(
                onSignUpClick = {
                    navController.navigate(NavDestination.Landing.route) {
                        popUpTo(NavDestination.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavDestination.Landing.route) {
                        popUpTo(NavDestination.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavDestination.Landing.route) {
            LandingScreen(
                onNavigateToRooms = {
                    navController.navigate(NavDestination.Rooms.route) {
                        popUpTo(NavDestination.Landing.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavDestination.Rooms.route) {
            RoomsScreen(
                onNavigateToRoom = { roomIntent ->
                    val roomId: String? =
                        when (roomIntent) {
                            RoomIntent.CreateRoom -> null
                            is RoomIntent.JoinRoom -> roomIntent.roomId
                        }
                    navController.navigate(NavDestination.Game.route + "?roomId=$roomId")
                },
            )
        }

        composable(NavDestination.Search.route) {
            SearchScreen()
        }

        composable(NavDestination.Statistics.route) {
            StatisticsScreen()
        }

        composable(NavDestination.Profile.route) {
            ProfileScreen()
        }

        composable(NavDestination.Create.route) {
            CreateScreen()
        }

        composable(
            route = NavDestination.Game.ROUTE_PATTERN,
            arguments =
                listOf(
                    navArgument(NavDestination.Game.ARG_ROOM_ID) {
                        type = NavType.StringType
                        nullable = true
                    },
                ),
        ) {
            GameScreen(
                onNavigateToRooms = {
                    navController.navigate(NavDestination.Rooms.route) {
                        popUpTo(NavDestination.Rooms.route) { inclusive = true }
                    }
                },
            )
        }
    }
}
