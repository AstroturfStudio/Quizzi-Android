package studio.astroturf.quizzi.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.ui.exceptionhandling.NotificationHandler
import studio.astroturf.quizzi.ui.navigation.QuizziBottomNavigation
import studio.astroturf.quizzi.ui.navigation.QuizziNavDestination
import studio.astroturf.quizzi.ui.navigation.QuizziNavHost
import studio.astroturf.quizzi.ui.theme.QuizziTheme

@Composable
fun QuizziApp(
    modifier: Modifier = Modifier,
    onBoardingCompleted: Boolean = false,
) {
    QuizziTheme {
        val navController = rememberNavController()
        var currentNotification by remember { mutableStateOf<UiNotification?>(null) }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val showBottomBar =
            when {
                currentDestination?.route in
                    listOf(
                        QuizziNavDestination.Onboarding.route,
                        QuizziNavDestination.Landing.route,
                        QuizziNavDestination.CreateRoom.route,
                        QuizziNavDestination.CategorySelection.route,
                        QuizziNavDestination.GameTypeSelection.route,
                        QuizziNavDestination.Game.route,
                        null,
                    )
                -> false
                currentDestination?.route?.startsWith(QuizziNavDestination.Game.route) == true -> false
                else -> true
            }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    QuizziBottomNavigation(
                        navController = navController,
                        currentDestination = currentDestination,
                        onFabClick = {
                            navController.navigate(QuizziNavDestination.CreateRoom.route)
                        },
                    )
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                QuizziNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    startDestination =
                        if (onBoardingCompleted) {
                            QuizziNavDestination.Landing.route
                        } else {
                            QuizziNavDestination.Onboarding.route
                        },
                )

                NotificationHandler(
                    notification = currentNotification,
                    onDismiss = { currentNotification = null },
                )
            }
        }
    }
}

@Preview
@Composable
private fun QuizziAppPreview() {
    QuizziApp()
}
