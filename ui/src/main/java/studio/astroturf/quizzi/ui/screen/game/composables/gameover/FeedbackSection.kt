package studio.astroturf.quizzi.ui.screen.game.composables.gameover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun FeedbackSection(
    onSubmitFeedback: (GameFeedback) -> Unit,
    onReportBug: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var enjoymentRating by remember { mutableStateOf<Int?>(null) }
    var difficultyRating by remember { mutableStateOf<Int?>(null) }
    var additionalFeedback by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (!isSubmitted) {
            Text(
                text = "Help us improve!",
                style = MaterialTheme.typography.titleLarge,
            )

            // Enjoyment Rating
            RatingQuestion(
                question = "How enjoyable was the game?",
                rating = enjoymentRating,
                onRatingSelect = { enjoymentRating = it },
            )

            // Difficulty Rating
            RatingQuestion(
                question = "How was the difficulty level?",
                rating = difficultyRating,
                onRatingSelect = { difficultyRating = it },
            )

            // Additional Feedback
            OutlinedTextField(
                value = additionalFeedback,
                onValueChange = { additionalFeedback = it },
                label = { Text("Any additional feedback?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Submit Feedback Button
                Button(
                    onClick = {
                        enjoymentRating?.let { enjoyment ->
                            difficultyRating?.let { difficulty ->
                                onSubmitFeedback(
                                    GameFeedback(
                                        enjoymentRating = enjoyment,
                                        difficultyRating = difficulty,
                                        additionalFeedback = additionalFeedback.takeIf { it.isNotBlank() },
                                        bugReport = null,
                                    ),
                                )
                                isSubmitted = true
                            }
                        }
                    },
                    enabled = enjoymentRating != null && difficultyRating != null,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Submit Feedback")
                }

                // Report Bug Button
                OutlinedButton(
                    onClick = onReportBug,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text("Report Bug")
                }
            }
        } else {
            Text(
                text = "Thank you for your feedback!",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun RatingQuestion(
    question: String,
    rating: Int?,
    onRatingSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            (1..5).forEach { score ->
                RatingButton(
                    score = score,
                    isSelected = rating == score,
                    onSelect = { onRatingSelect(score) },
                )
            }
        }
    }
}

@Composable
private fun RatingButton(
    score: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Surface(
        onClick = onSelect,
        modifier = Modifier.size(48.dp),
        shape = MaterialTheme.shapes.medium,
        color =
            if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        border =
            BorderStroke(
                1.dp,
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
            ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
