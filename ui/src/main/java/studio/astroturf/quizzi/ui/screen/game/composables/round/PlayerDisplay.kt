package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import studio.astroturf.quizzi.domain.model.PlayerInRoom
import studio.astroturf.quizzi.domain.model.PlayerState
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodySmallMedium
import studio.astroturf.quizzi.ui.theme.BodyXSmallMedium
import studio.astroturf.quizzi.ui.theme.QuizziTheme

@Composable
internal fun PlayerDisplay(
    player: PlayerInRoom,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    isSmallScreen: Boolean = false,
) {
    // Adjust sizes based on screen size
    val avatarSize = if (isSmallScreen) 32.dp else 44.dp
    val spacerSize = if (isSmallScreen) 2.dp else 4.dp
    val textBoxHeight = if (isSmallScreen) 18.dp else 22.dp
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(player.avatarUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = "Player avatar",
            modifier = Modifier.size(avatarSize),
        )

        Spacer(modifier = Modifier.size(spacerSize))

        Box(
            modifier = Modifier.height(textBoxHeight),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = player.name,
                style = if (isSmallScreen) BodyXSmallMedium else BodySmallMedium,
                color = Black,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerDisplayPreview() {
    QuizziTheme {
        PlayerDisplay(
            player =
                PlayerInRoom(
                    id = "1",
                    name = "Güven",
                    avatarUrl = "https://example.com/avatar.png",
                    state = PlayerState.READY,
                ),
            imageLoader = ImageLoader(LocalContext.current),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerDisplaySmallScreenPreview() {
    QuizziTheme {
        PlayerDisplay(
            player =
                PlayerInRoom(
                    id = "1",
                    name = "Güven",
                    avatarUrl = "https://example.com/avatar.png",
                    state = PlayerState.READY,
                ),
            imageLoader = ImageLoader(LocalContext.current),
            isSmallScreen = true,
        )
    }
}
