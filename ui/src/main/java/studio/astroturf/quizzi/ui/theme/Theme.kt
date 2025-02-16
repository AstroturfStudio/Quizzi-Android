package studio.astroturf.quizzi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val colorScheme =
    lightColorScheme(
        primary = Primary,
        onPrimary = White,
        secondary = Secondary,
        onSecondary = White,
        tertiary = Tertiary,
    )

private val typography =
    Typography(
        headlineLarge = Heading1,
        headlineMedium = Heading2,
        headlineSmall = Heading3,
        titleMedium = BodyNormalMedium,
        titleSmall = BodySmallMedium,
        bodyLarge = BodyNormalRegular,
        bodyMedium = BodySmallRegular,
        bodySmall = BodyXSmallRegular,
        labelLarge = TextSmall,
        labelMedium = TextXSmall,
    )

@Composable
fun QuizziTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}
