package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.theme.Heading3
import studio.astroturf.quizzi.ui.theme.Heading4
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.Tertiary
import studio.astroturf.quizzi.ui.theme.White

@Composable
internal fun TimeDisplay(
    totalTime: Int,
    timeLeft: Int,
    modifier: Modifier = Modifier,
    isSmallScreen: Boolean = false,
    onTimeChange: (Int) -> Unit = {},
) {
    val animatedSweepAngle = remember { Animatable(360f - (360f * (timeLeft.toFloat() / totalTime))) }

    // Adjust size based on screen size
    val timerSize = if (isSmallScreen) 48.dp else 64.dp

    LaunchedEffect(key1 = timeLeft) {
        animatedSweepAngle.animateTo(
            targetValue = if (timeLeft > 0) 360f - (360f * (timeLeft.toFloat() / totalTime)) else 360f,
            animationSpec = tween(500), // Adjust animation speed as needed
        )
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(timerSize)) {
            val sweepAngle = animatedSweepAngle.value
            val startAngle = -90f // Clockwise

            // Draw the elapsed time arc
            drawArc(
                color = Tertiary,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height),
                topLeft = Offset.Zero,
            )

            // Draw the remaining time arc
            drawArc(
                color = Primary,
                startAngle = startAngle + sweepAngle,
                sweepAngle = 360f - sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height),
                topLeft = Offset.Zero,
            )
        }
        Text(
            text = timeLeft.toString(),
            style = if (isSmallScreen) Heading4 else Heading3,
            color = White,
        )
    }
}

@Preview
@Composable
private fun TimeDisplayPreview() {
    QuizziTheme {
        TimeDisplay(
            totalTime = 10,
            timeLeft = 8,
            onTimeChange = {},
        )
    }
}

@Preview
@Composable
private fun TimeDisplaySmallScreenPreview() {
    QuizziTheme {
        TimeDisplay(
            totalTime = 10,
            timeLeft = 8,
            onTimeChange = {},
            isSmallScreen = true,
        )
    }
}
