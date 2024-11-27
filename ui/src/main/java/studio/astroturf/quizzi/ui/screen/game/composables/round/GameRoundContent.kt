package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.domain.model.Option
import studio.astroturf.quizzi.domain.model.Player
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.ui.screen.game.GameUiState
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import studio.astroturf.quizzi.ui.theme.QuizziTheme

@Composable
internal fun GameRoundContent(
    state: GameUiState.RoundOn,
    onSubmitAnswer: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // GameTopBar needs specific height constraints
        Box(modifier = Modifier.height(56.dp)) {
            GameTopBar(
                timeRemaining = state.timeRemainingInSeconds,
                cursorPosition = 1 - state.gameBarPercentage, // fixme
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Question takes remaining available space
        QuestionContent(
            question = state.question,
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Answer grid at the bottom with specific padding
        AnswerGrid(
            question = state.question,
            selectedAnswerId = state.selectedAnswerId,
            playerRoundResult = state.playerRoundResult,
            onAnswerSelected = onSubmitAnswer,
            modifier = Modifier.padding(bottom = 16.dp),
        )
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
                    selectedAnswerId = 1, // Paris seçili
                    playerRoundResult = null, // Henüz sonuç gelmemiş
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
                    selectedAnswerId = 1, // Paris seçili
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
