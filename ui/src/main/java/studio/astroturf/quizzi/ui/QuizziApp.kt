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
import studio.astroturf.quizzi.ui.navigation.QuizziNavGraph
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.navigation.NavDestination as QuizziNavDestination

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
            if (currentDestination?.route == QuizziNavDestination.Onboarding.route ||
                currentDestination?.route == QuizziNavDestination.Landing.route ||
                currentDestination?.route == QuizziNavDestination.Create.route ||
                currentDestination?.route == QuizziNavDestination.Game.route ||
                currentDestination?.route == null
            ) {
                false
            } else if (
                currentDestination.route != null &&
                currentDestination.route!!.startsWith(QuizziNavDestination.Game.route)
            ) {
                false
            } else {
                true
            }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    QuizziBottomNavigation(
                        navController = navController,
                        currentDestination = currentDestination,
                        onFabClick = {
                            navController.navigate(QuizziNavDestination.Game.route)
                        },
                    )
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                QuizziNavGraph(
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
