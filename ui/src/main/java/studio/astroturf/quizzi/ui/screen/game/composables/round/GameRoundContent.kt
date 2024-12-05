package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import studio.astroturf.quizzi.domain.model.Option
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.ui.screen.game.GameUiState
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import studio.astroturf.quizzi.ui.theme.QuizziTheme

@Composable
fun GameRoundContent(
    state: GameUiState.RoundOn,
    onSubmitAnswer: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .systemBarsPadding(),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f) // This makes it fill remaining space
                    .fillMaxWidth()
                    .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = state.question.content)
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = state.question.imageUrl,
                contentDescription = "Question Image",
                modifier = Modifier.width(320.dp).wrapContentHeight(),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimeDisplay(
                modifier = Modifier.size(42.dp),
                timeRemaining = state.timeRemainingInSeconds,
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlayerDisplay(
                    modifier = Modifier.weight(1f),
                    player = state.player1,
                    isLeft = true,
                )

                GameBar(
                    modifier =
                        Modifier
                            .width(160.dp)
                            .height(12.dp),
                    cursorPosition = 1 - state.gameBarPercentage,
                )

                PlayerDisplay(
                    modifier = Modifier.weight(1f),
                    player = state.player2,
                    isLeft = false,
                )
            }

            // Answer grid at the bottom
            AnswerGrid(
                modifier =
                    Modifier
                        .wrapContentHeight(),
                question = state.question,
                selectedAnswerId = state.selectedAnswerId,
                playerRoundResult = state.playerRoundResult,
                onAnswerSelected = onSubmitAnswer,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentPreview() {
    QuizziTheme {
        GameRoundContent(
            state =
                GameUiState.RoundOn(
                    player1 =
                        Player(
                            id = "1",
                            name = "Player 1",
                            avatarUrl = "TODO()",
                        ),
                    player2 =
                        Player(
                            id = "2",
                            name = "Player 2",
                            avatarUrl = "TODO()",
                        ),
                    gameBarPercentage = 0.7f,
                    question =
                        Question(
                            content = "What is the capital of France?",
                            imageUrl = "https://example.com/paris.jpg",
                            options =
                                listOf(
                                    Option(id = 1, value = "Paris"),
                                    Option(id = 2, value = "London"),
                                    Option(id = 3, value = "Berlin"),
                                    Option(id = 4, value = "Madrid"),
                                ),
                        ),
                    timeRemainingInSeconds = 14,
                    selectedAnswerId = null,
                    playerRoundResult = null,
                ),
            onSubmitAnswer = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentWithSelectedAnswerPreview() {
    QuizziTheme {
        GameRoundContent(
            state =
                GameUiState.RoundOn(
                    player1 =
                        Player(
                            id = "1",
                            name = "Player 1",
                            avatarUrl = "TODO()",
                        ),
                    player2 =
                        Player(
                            id = "2",
                            name = "Player 2",
                            avatarUrl = "TODO()",
                        ),
                    gameBarPercentage = 0.7f,
                    question =
                        Question(
                            content = "What is the capital of France?",
                            imageUrl = "https://example.com/paris.jpg",
                            options =
                                listOf(
                                    Option(id = 1, value = "Paris"),
                                    Option(id = 2, value = "London"),
                                    Option(id = 3, value = "Berlin"),
                                    Option(id = 4, value = "Madrid"),
                                ),
                        ),
                    timeRemainingInSeconds = 14,
                    selectedAnswerId = 1,
                    playerRoundResult = null,
                ),
            onSubmitAnswer = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentWithCorrectAnswerPreview() {
    QuizziTheme {
        GameRoundContent(
            state =
                GameUiState.RoundOn(
                    player1 =
                        Player(
                            id = "1",
                            name = "Player 1",
                            avatarUrl = "TODO()",
                        ),
                    player2 =
                        Player(
                            id = "2",
                            name = "Player 2",
                            avatarUrl = "TODO()",
                        ),
                    gameBarPercentage = 0.7f,
                    question =
                        Question(
                            content = "What is the capital of France?",
                            imageUrl = "https://example.com/paris.jpg",
                            options =
                                listOf(
                                    Option(id = 1, value = "Paris"),
                                    Option(id = 2, value = "London"),
                                    Option(id = 3, value = "Berlin"),
                                    Option(id = 4, value = "Madrid"),
                                ),
                        ),
                    timeRemainingInSeconds = 14,
                    selectedAnswerId = 1,
                    playerRoundResult =
                        PlayerRoundResult(
                            answerId = 1,
                            isCorrect = true,
                        ),
                ),
            onSubmitAnswer = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentWithWrongAnswerPreview() {
    QuizziTheme {
        GameRoundContent(
            state =
                GameUiState.RoundOn(
                    player1 =
                        Player(
                            id = "1",
                            name = "Player 1",
                            avatarUrl = "TODO()",
                        ),
                    player2 =
                        Player(
                            id = "2",
                            name = "Player 2",
                            avatarUrl = "TODO()",
                        ),
                    gameBarPercentage = 0.7f,
                    question =
                        Question(
                            content = "What is the capital of France?",
                            imageUrl = "https://example.com/paris.jpg",
                            options =
                                listOf(
                                    Option(id = 1, value = "Paris"),
                                    Option(id = 2, value = "London"),
                                    Option(id = 3, value = "Berlin"),
                                    Option(id = 4, value = "Madrid"),
                                ),
                        ),
                    timeRemainingInSeconds = 14,
                    selectedAnswerId = 1,
                    playerRoundResult =
                        PlayerRoundResult(
                            answerId = 1,
                            isCorrect = false,
                        ),
                ),
            onSubmitAnswer = {},
        )
    }
}
