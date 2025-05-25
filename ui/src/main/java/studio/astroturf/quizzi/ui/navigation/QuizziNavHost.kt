package studio.astroturf.quizzi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import navigateSafely
import studio.astroturf.quizzi.ui.screen.create.CreateRoomScreen
import studio.astroturf.quizzi.ui.screen.create.category.CategorySelectionScreen
import studio.astroturf.quizzi.ui.screen.create.gametype.GameTypeSelectionScreen
import studio.astroturf.quizzi.ui.screen.game.GameScreen
import studio.astroturf.quizzi.ui.screen.landing.LandingScreen
import studio.astroturf.quizzi.ui.screen.onboarding.OnboardingScreen
import studio.astroturf.quizzi.ui.screen.profile.ProfileScreen
import studio.astroturf.quizzi.ui.screen.rooms.RoomsScreen
import studio.astroturf.quizzi.ui.screen.search.SearchScreen
import studio.astroturf.quizzi.ui.screen.statistics.StatisticsScreen

@Composable
fun QuizziNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = QuizziNavDestination.Onboarding.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(QuizziNavDestination.Onboarding.route) {
            OnboardingScreen(
                onSignUpClick = {
                    navController.navigate(QuizziNavDestination.Landing.route) {
                        popUpTo(QuizziNavDestination.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(QuizziNavDestination.Landing.route) {
                        popUpTo(QuizziNavDestination.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

        composable(QuizziNavDestination.Landing.route) {
            LandingScreen(
                onNavigateToRooms = {
                    navController.navigate(QuizziNavDestination.Rooms.route) {
                        popUpTo(QuizziNavDestination.Landing.route) { inclusive = true }
                    }
                },
            )
        }

        composable(QuizziNavDestination.Rooms.route) {
            RoomsScreen(
                onNavigateToRoom = { room ->
                    navController.navigateSafely(
                        QuizziNavDestination.Game.createRouteForJoining(
                            roomId = room.id,
                            roomName = room.name,
                            categoryId =
                                room.category.id.id
                                    .toString(),
                            gameType = room.gameType,
                        ),
                    )
                },
            )
        }

        composable(QuizziNavDestination.Search.route) {
            SearchScreen()
        }

        composable(QuizziNavDestination.Statistics.route) {
            StatisticsScreen()
        }

        composable(QuizziNavDestination.Profile.route) {
            ProfileScreen()
        }

        addCreateRoomGraph(navController)

        composable(
            route = QuizziNavDestination.Game.route,
            arguments =
                listOf(
                    navArgument(QuizziNavDestination.Game.ARG_ROOM_ID) {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument(QuizziNavDestination.Game.ARG_ROOM_NAME) {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument(QuizziNavDestination.Game.ARG_CATEGORY_ID) {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument(QuizziNavDestination.Game.ARG_GAME_TYPE) {
                        type = NavType.StringType
                        nullable = true
                    },
                ),
            deepLinks =
                listOf(
                    androidx.navigation.navDeepLink {
                        uriPattern =
                            "android-app://androidx.navigation/game?roomName={roomName}&categoryId={categoryId}&gameType={gameType}"
                    },
                ),
        ) {
            GameScreen(
                onNavigateToRooms = {
                    navController.navigate(QuizziNavDestination.Rooms.route) {
                        popUpTo(QuizziNavDestination.Rooms.route) { inclusive = true }
                    }
                },
            )
        }
    }
}

private fun NavGraphBuilder.addCreateRoomGraph(navController: NavHostController) {
    navigation(
        route = QuizziNavGraph.CreateRoom.route,
        startDestination = QuizziNavDestination.CreateRoom.route,
    ) {
        composable(QuizziNavDestination.CreateRoom.route) {
            CreateRoomScreen(
                navController = navController,
                onBackPress = {
                    navController.popBackStack()
                },
                onCategoryClick = {
                    navController.navigate(QuizziNavDestination.CategorySelection.route)
                },
                onGameTypeClick = {
                    navController.navigate(QuizziNavDestination.GameTypeSelection.route)
                },
                onCreateRoom = { roomName, category, gameType ->
                    navController.navigateSafely(
                        QuizziNavDestination.Game.createRouteForCreating(roomName, category.id.id.toString(), gameType.name),
                    ) {
                        popUpTo(QuizziNavGraph.CreateRoom.route) { inclusive = true }
                    }
                },
            )
        }

        composable(QuizziNavDestination.CategorySelection.route) {
            CategorySelectionScreen(
                onBackPress = {
                    navController.popBackStack()
                },
                onProceed = { selectedCategory ->
                    navController.run {
                        previousBackStackEntry?.savedStateHandle?.set(
                            QuizziNavDestination.CreateRoom.ARG_SELECTED_QUIZ_CATEGORY,
                            selectedCategory,
                        )
                        navigateUp()
                    }
                },
            )
        }

        composable(QuizziNavDestination.GameTypeSelection.route) {
            GameTypeSelectionScreen(
                onBackPress = {
                    navController.popBackStack()
                },
                onProceed = { selectedGameType ->
                    navController.run {
                        previousBackStackEntry?.savedStateHandle?.set(
                            QuizziNavDestination.CreateRoom.ARG_SELECTED_GAME_TYPE,
                            selectedGameType,
                        )
                        navigateUp()
                    }
                },
            )
        }
    }
}
