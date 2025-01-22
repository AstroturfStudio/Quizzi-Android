package studio.astroturf.quizzi.ui.screen.game.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun CachedQuestionImage(
    countryCode: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Generate the resource ID for the corresponding drawable
    val resourceId =
        context.resources.getIdentifier(
            "${countryCode}_${IMAGE_HEIGHT_PX_LARGE}", // Assuming drawable names are in this format
            "drawable",
            context.packageName,
        )

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = "Question Image",
        modifier = modifier,
        contentScale = ContentScale.FillHeight,
    )
}

private const val IMAGE_HEIGHT_PX_LARGE = 320
private const val IMAGE_HEIGHT_PX_SMALL = 160
