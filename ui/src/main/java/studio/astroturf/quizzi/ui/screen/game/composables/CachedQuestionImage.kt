package studio.astroturf.quizzi.ui.screen.game.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun CachedQuestionImage(
    imageUrl: String?,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .diskCacheKey(imageUrl)
                .memoryCacheKey(imageUrl)
                .build(),
        contentDescription = "Question Image",
        imageLoader = imageLoader,
        modifier = modifier,
    )
}
