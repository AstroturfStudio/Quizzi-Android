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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import studio.astroturf.quizzi.domain.model.PlayerInRoom
import studio.astroturf.quizzi.domain.model.PlayerState
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodySmallMedium
import studio.astroturf.quizzi.ui.theme.QuizziTheme

@Composable
internal fun PlayerDisplay(
    player: PlayerInRoom,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
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
            modifier = Modifier.size(44.dp),
        )

        Spacer(modifier = Modifier.size(4.dp))

        Box(
            modifier = Modifier.height(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = player.name,
                style = BodySmallMedium,
                color = Black,
                textAlign = TextAlign.Center,
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
