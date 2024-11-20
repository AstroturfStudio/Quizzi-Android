import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

fun Modifier.pulseAnimation(
    pulseScale: Float = 1.2f,
    duration: Int = 1000,
    repeatMode: RepeatMode = RepeatMode.Reverse
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration),
            repeatMode = repeatMode
        ),
        label = "pulse"
    )
    this.scale(scale)
}

fun Modifier.shimmerEffect(
    duration: Int = 1000,
    delay: Int = 300,
    minAlpha: Float = 0.2f,
    maxAlpha: Float = 0.9f
): Modifier = composed {
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isAnimating = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    this.graphicsLayer(alpha = if (isAnimating) alpha else minAlpha)
}

fun Modifier.rotateAnimation(
    duration: Int = 2000,
    repeatMode: RepeatMode = RepeatMode.Restart
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration),
            repeatMode = repeatMode
        ),
        label = "rotate"
    )

    this.graphicsLayer(rotationZ = rotation)
}

fun Modifier.shake(
    enabled: Boolean,
    amplitude: Float = 10f,
    duration: Int = 300
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val offset by infiniteTransition.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(duration / 2),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    graphicsLayer {
        translationX = if (enabled) offset else 0f
    }
}