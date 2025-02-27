
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import timber.log.Timber

fun NavController.navigateSafely(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    try {
        navigate(route, builder)
    } catch (e: IllegalArgumentException) {
        // Log the error with details to help debugging
        Timber.tag("Navigation").e("Failed to navigate to $route: ${e.message}")
    }
}

fun NavController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
}

fun NavController.clearBackStack() {
    val startDestination = graph.startDestinationId
    popBackStack(startDestination, false)
}

fun NavController.isCurrentDestination(route: String): Boolean = currentDestination?.route == route

fun NavController.navigateWithAnimation(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route) {
        anim {
            enter = android.R.anim.fade_in
            exit = android.R.anim.fade_out
            popEnter = android.R.anim.fade_in
            popExit = android.R.anim.fade_out
        }
        builder()
    }
}
