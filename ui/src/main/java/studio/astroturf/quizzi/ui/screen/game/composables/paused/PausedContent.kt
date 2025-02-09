package studio.astroturf.quizzi.ui.screen.game.composables.paused

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import studio.astroturf.quizzi.domain.model.PlayerInRoom

@Composable
internal fun PausedContent(
    reason: String,
    onlinePlayers: List<PlayerInRoom>,
    onRetry: () -> Unit,
) {
    Column(/*...*/) {
        Text(reason)
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
