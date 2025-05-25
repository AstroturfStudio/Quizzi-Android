package studio.astroturf.quizzi.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun QuizziIcon(
    @DrawableRes iconResId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    Icon(
        painter = painterResource(id = iconResId),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}
