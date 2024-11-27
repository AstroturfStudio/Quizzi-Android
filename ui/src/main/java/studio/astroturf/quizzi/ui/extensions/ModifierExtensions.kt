import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    composed {
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
        ) {
            onClick()
        }
    }

fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier,
): Modifier =
    if (condition) {
        then(modifier())
    } else {
        this
    }

fun Modifier.bottomBorder(
    strokeWidth: Dp = 1.dp,
    color: Color = Color.Gray,
) = drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidth.toPx(),
    )
}

fun Modifier.topBorder(
    strokeWidth: Dp = 1.dp,
    color: Color = Color.Gray,
) = drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = strokeWidth.toPx(),
    )
}

fun Modifier.defaultPadding() = padding(16.dp)

fun Modifier.debugBorder(color: Color = Color.Red) = border(1.dp, color)

fun Modifier.circularBorder(
    color: Color,
    strokeWidth: Dp = 1.dp,
) = border(
    width = strokeWidth,
    color = color,
    shape = CircleShape,
)
