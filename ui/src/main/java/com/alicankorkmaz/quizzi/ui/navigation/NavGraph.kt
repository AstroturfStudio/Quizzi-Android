import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alicankorkmaz.quizzi.ui.screen.game.GameScreen
import com.alicankorkmaz.quizzi.ui.screen.landing.LandingScreen

const val ROUTE_LANDING = "landing"
const val ROUTE_ROOMS = "rooms"
const val ROUTE_GAME = "game"

@Composable
fun QuizziNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = ROUTE_LANDING
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUTE_LANDING) {
            LandingScreen(
                onNavigateToRooms = {
                    navController.navigate(ROUTE_ROOMS) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                }
            )
        }

        composable(ROUTE_ROOMS) {
            RoomsScreen(
                onNavigateToRoom = {
                    navController.navigate(ROUTE_GAME) {
                        popUpTo(ROUTE_ROOMS) { saveState = true }
                    }
                }
            )
        }

        composable(ROUTE_GAME) {
            GameScreen()
        }
    }
} 