package studio.astroturf.quizzi.ui.screen.game.composables.roundend

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RoundResultOverlay(
    correctAnswerText: String,
    roundWinner: RoundWinner,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInHorizontally(),
        modifier = modifier,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.padding(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Doğru Cevap:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = correctAnswerText,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text =
                            when (roundWinner) {
                                is RoundWinner.Me -> "Tebrikler! Bu eli kazandınız!"
                                RoundWinner.None -> "Cevabi bilen olmadi. Berabere!"
                                is RoundWinner.Opponent -> "${roundWinner.name} bu eli kazandı!"
                            },
                        style = MaterialTheme.typography.titleMedium,
                        color =
                            when (roundWinner) {
                                is RoundWinner.Me -> Color(0xFF4CAF50)
                                RoundWinner.None -> MaterialTheme.colorScheme.onSurface
                                is RoundWinner.Opponent -> Color(0xFFAF4C4C)
                            },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RoundResultOverlayPreview() {
    RoundResultOverlay(
        correctAnswerText = "Doğru Cevap",
        roundWinner = RoundWinner.Me("", "Alican"),
    )
}
