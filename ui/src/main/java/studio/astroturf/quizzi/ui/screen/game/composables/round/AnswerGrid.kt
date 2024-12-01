package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult

@Composable
internal fun AnswerGrid(
    question: Question,
    selectedAnswerId: Int?,
    playerRoundResult: PlayerRoundResult?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        question.options.forEach { option ->
            AnswerButton(
                text = option.value,
                optionId = option.id,
                selectedAnswerId = selectedAnswerId,
                playerRoundResult = playerRoundResult,
                onClick = { onAnswerSelected(option.id) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
            )
        }
    }
}
