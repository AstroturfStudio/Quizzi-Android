package studio.astroturf.quizzi.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import studio.astroturf.quizzi.ui.components.QuizziIcon
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.navigation.NavDestination as QuizziNavDestination

sealed class BottomNavIcon {
    data class ResourceIcon(
        @DrawableRes val resId: Int,
    ) : BottomNavIcon()

    data class VectorIcon(
        val imageVector: ImageVector,
    ) : BottomNavIcon()
}

data class BottomNavItem(
    val route: String,
    val icon: BottomNavIcon,
    val label: String,
)

val bottomNavItems =
    listOf(
        BottomNavItem(
            route = QuizziNavDestination.Home.route,
            icon = BottomNavIcon.VectorIcon(Icons.Default.Home),
            label = "Home",
        ),
        BottomNavItem(
            route = QuizziNavDestination.Search.route,
            icon = BottomNavIcon.VectorIcon(Icons.Default.Search),
            label = "Search",
        ),
        BottomNavItem(
            route = QuizziNavDestination.Statistics.route,
            icon = BottomNavIcon.VectorIcon(Icons.Default.Menu),
            label = "Stats",
        ),
        BottomNavItem(
            route = QuizziNavDestination.Profile.route,
            icon = BottomNavIcon.VectorIcon(Icons.Default.Person),
            label = "Profile",
        ),
    )

@Composable
fun QuizziBottomNavigation(
    navController: NavHostController,
    currentDestination: NavDestination?,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        NavigationBar(
            modifier =
                Modifier
                    .height(85.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // First half of items
                bottomNavItems.take(2).forEach { item ->
                    NavigationBarItem(
                        icon = {
                            when (val icon = item.icon) {
                                is BottomNavIcon.ResourceIcon -> {
                                    QuizziIcon(
                                        iconResId = icon.resId,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                                is BottomNavIcon.VectorIcon -> {
                                    Icon(
                                        imageVector = icon.imageVector,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }

                // Space for FAB
                Spacer(modifier = Modifier.width(80.dp))

                // Second half of items
                bottomNavItems.takeLast(2).forEach { item ->
                    NavigationBarItem(
                        icon = {
                            when (val icon = item.icon) {
                                is BottomNavIcon.ResourceIcon -> {
                                    QuizziIcon(
                                        iconResId = icon.resId,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                                is BottomNavIcon.VectorIcon -> {
                                    Icon(
                                        imageVector = icon.imageVector,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onFabClick,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-22).dp)
                    .size(48.dp),
            containerColor = Primary,
            shape = CircleShape,
            elevation =
                FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp,
                ),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Game",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
