package studio.astroturf.quizzi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = Primary,
        secondary = Secondary,
        tertiary = Tertiary,
        // Accent colors
        error = Accent1,
        errorContainer = Accent1,
        onError = White,
        onErrorContainer = White,
        // Surface and background colors
        surface = Grey1,
        onSurface = White,
        surfaceVariant = Grey2,
        onSurfaceVariant = Grey5,
        background = Black,
        onBackground = White,
        // Additional colors
        outline = Grey3,
        outlineVariant = Grey2,
        scrim = Black.copy(alpha = 0.3f),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Primary,
        secondary = Secondary,
        tertiary = Tertiary,
        // Accent colors
        error = Accent1,
        errorContainer = Accent1,
        onError = White,
        onErrorContainer = White,
        // Surface and background colors
        surface = White,
        onSurface = Black,
        surfaceVariant = Grey5,
        onSurfaceVariant = Grey1,
        background = Grey5,
        onBackground = Black,
        // Additional colors
        outline = Grey2,
        outlineVariant = Grey3,
        scrim = Black.copy(alpha = 0.1f),
    )

@Composable
fun QuizziTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
