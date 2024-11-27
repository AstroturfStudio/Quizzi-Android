package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.ui.theme.QuizziTheme

@Composable
internal fun PlayerDisplay(
    player: Player,
    isLeft: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!isLeft) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp),
            )
        }

        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp),
        ) {
            // Player avatar would go here
            // For now just showing a colored surface
        }

        if (isLeft) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

// Additional preview for PlayerDisplay
@Preview(showBackground = true)
@Composable
private fun PlayerDisplayPreview() {
    QuizziTheme {
        Row(modifier = Modifier.width(200.dp)) {
            PlayerDisplay(
                player =
                    Player(
                        id = "1",
                        name = "John Doe",
                        avatarUrl = "TODO()",
                    ),
                isLeft = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerDisplayRightPreview() {
    QuizziTheme {
        Row(modifier = Modifier.width(200.dp)) {
            PlayerDisplay(
                player =
                    Player(
                        id = "2",
                        name = "Jane Doe",
                        avatarUrl = "TODO()",
                    ),
                isLeft = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
