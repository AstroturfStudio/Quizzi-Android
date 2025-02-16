package studio.astroturf.quizzi.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.QuizziIcon
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.White

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
    @StringRes val labelStringRes: Int,
)

val bottomNavItems =
    listOf(
        BottomNavItem(
            route = QuizziNavDestination.Rooms.route,
            icon = BottomNavIcon.VectorIcon(Icons.Default.Home),
            labelStringRes = R.string.home,
        ),
//        BottomNavItem(
//            route = QuizziNavDestination.Search.route,
//            icon = BottomNavIcon.VectorIcon(Icons.Default.Search),
//            label = "Search",
//        ),
//        BottomNavItem(
//            route = QuizziNavDestination.Statistics.route,
//            icon = BottomNavIcon.VectorIcon(Icons.Default.Menu),
//            label = "Stats",
//        ),
//        BottomNavItem(
//            route = QuizziNavDestination.Profile.route,
//            icon = BottomNavIcon.VectorIcon(Icons.Default.Person),
//            label = "Profile",
//        ),
    )

private class BottomNavigationShape(
    private val fabSize: Float,
    private val fabPadding: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val centerX = size.width / 2
        val cutoutRadius = (fabSize / 2) + fabPadding

        return Outline.Generic(
            Path().apply {
                // Start from top-left with rounded corner
                moveTo(0f, 20f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                lineTo(size.width, 20f)

                // Top rounded corners
                arcTo(
                    rect =
                        Rect(
                            left = 0f,
                            top = 0f,
                            right = 40f,
                            bottom = 40f,
                        ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                lineTo(centerX - cutoutRadius, 0f)

                // Left cutout curve
                arcTo(
                    rect =
                        Rect(
                            left = centerX - cutoutRadius,
                            top = 0f,
                            right = centerX + cutoutRadius,
                            bottom = 2 * cutoutRadius,
                        ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false,
                )

                lineTo(size.width - 20f, 0f)

                // Top right corner
                arcTo(
                    rect =
                        Rect(
                            left = size.width - 40f,
                            top = 0f,
                            right = size.width,
                            bottom = 40f,
                        ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                close()
            },
        )
    }
}

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
                    .wrapContentHeight()
                    .background(Color.Transparent),
            tonalElevation = 8.dp,
            containerColor = Color.Transparent,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // First half of items
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            when (val icon = item.icon) {
                                is BottomNavIcon.ResourceIcon -> {
                                    QuizziIcon(
                                        iconResId = icon.resId,
                                        contentDescription = stringResource(item.labelStringRes),
                                        modifier = Modifier.size(24.dp),
                                    )
                                }

                                is BottomNavIcon.VectorIcon -> {
                                    Icon(
                                        imageVector = icon.imageVector,
                                        contentDescription = stringResource(item.labelStringRes),
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        },
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
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
                    .offset(y = (-24).dp)
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
                tint = White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
