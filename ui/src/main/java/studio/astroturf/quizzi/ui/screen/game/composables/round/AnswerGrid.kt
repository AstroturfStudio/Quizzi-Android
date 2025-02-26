package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.domain.model.Option
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult

@Composable
internal fun AnswerGrid(
    question: Question,
    selectedAnswerId: Int?,
    playerRoundResult: PlayerRoundResult?,
    onAnswerSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Get screen height to make the component responsive
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    
    // Calculate button height and spacing based on screen size
    // For smaller screens, reduce the button height and spacing significantly
    val buttonHeight = when {
        screenHeight < 600 -> 40.dp
        screenHeight < 720 -> 48.dp
        else -> 56.dp
    }
    
    val buttonSpacing = when {
        screenHeight < 600 -> 6.dp
        screenHeight < 720 -> 8.dp
        else -> 10.dp
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = if (screenHeight < 600) 4.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing),
    ) {
        question.options.forEach { option ->
            AnswerButton(
                text = option.value,
                optionId = option.id,
                selectedAnswerId = selectedAnswerId,
                playerRoundResult = playerRoundResult,
                onClick = { onAnswerSelect(option.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
            )
        }
    }
}

@Preview
@Composable
private fun AnswerGridPreview() {
    AnswerGrid(
        question =
            Question(
                countryCode = "af",
                content = "What is the capital of Turkey?",
                options =
                    listOf(
                        Option(id = 1, value = "Ankara"),
                        Option(id = 2, value = "Istanbul"),
                        Option(id = 3, value = "Izmir"),
                        Option(id = 4, value = "Bursa"),
                    ),
            ),
        selectedAnswerId = 2,
        playerRoundResult = null,
        onAnswerSelect = {},
    )
}
