import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

fun Color.toHex(): String {
    return String.format(
        "#%02X%02X%02X%02X",
        (alpha * 255).roundToInt(),
        (red * 255).roundToInt(),
        (green * 255).roundToInt(),
        (blue * 255).roundToInt()
    )
}