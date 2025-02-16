package studio.astroturf.quizzi.ui.screen.game.composables.round

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import studio.astroturf.quizzi.domain.model.Option
import studio.astroturf.quizzi.domain.model.PlayerInRoom
import studio.astroturf.quizzi.domain.model.PlayerState
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.ui.screen.game.GameUiState
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import studio.astroturf.quizzi.ui.screen.game.composables.CachedQuestionImage
import studio.astroturf.quizzi.ui.theme.BodyXLarge
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.White

@Composable
fun GameRoundContent(
    gameType: String,
    state: GameUiState.RoundOn,
    onSubmitAnswer: (Int) -> Unit,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = Primary)
                .padding(8.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = White, shape = RoundedCornerShape(32.dp))
                    .padding(horizontal = 16.dp, vertical = 36.dp)
                    .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.question.countryCode?.let {
                CachedQuestionImage(
                    countryCode = it,
                    modifier = Modifier.width(320.dp).height(160.dp),
                )
            }

            Box(
                modifier =
                    Modifier
                        .height(64.dp)
                        .fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = state.question.content,
                    style = BodyXLarge,
                    textAlign = TextAlign.Center, // Still useful for horizontal alignment
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            if (state.player2 != null) {
                VersusDisplay(state.player1, state.player2, state.timeRemainingInSeconds, imageLoader)
            } else {
                TimeDisplay(
                    totalTime = getTotalTime(gameType),
                    timeLeft = state.timeRemainingInSeconds,
                    modifier = Modifier.wrapContentSize(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GameBar(
                modifier =
                    Modifier
                        .padding(horizontal = 36.dp)
                        .fillMaxWidth()
                        .height(8.dp),
                cursorPosition = 1 - state.gameBarPercentage,
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                AnswerGrid(
                    modifier =
                        Modifier
                            .wrapContentHeight(),
                    question = state.question,
                    selectedAnswerId = state.selectedAnswerId,
                    playerRoundResult = state.playerRoundResult,
                    onAnswerSelect = onSubmitAnswer,
                )
            }
        }
    }
}

fun getTotalTime(gameType: String): Int =
    when (gameType) {
        "ResistanceGame" -> 10
        "ResistToTimeGame" -> 3
        else -> 10
    }

@Composable
private fun VersusDisplay(
    player1: PlayerInRoom,
    player2: PlayerInRoom,
    timeRemainingInSeconds: Int,
    imageLoader: ImageLoader,
) {
    Row(
        modifier =
            Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerDisplay(
            player = player1,
            imageLoader = imageLoader,
        )

        TimeDisplay(
            totalTime = 10,
            timeLeft = timeRemainingInSeconds,
            modifier = Modifier.wrapContentSize(),
        )

        PlayerDisplay(
            player = player2,
            imageLoader = imageLoader,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentPreview() {
    val context = LocalContext.current
    QuizziTheme {
        GameRoundContent(
            gameType = "ResistanceGame",
            state = previewGameState(),
            onSubmitAnswer = {},
            imageLoader = previewImageLoader(context),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentWithSelectedAnswerPreview() {
    val context = LocalContext.current
    QuizziTheme {
        GameRoundContent(
            gameType = "ResistanceGame",
            state = previewGameState(selectedAnswerId = 1),
            onSubmitAnswer = {},
            imageLoader = previewImageLoader(context),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentWithCorrectAnswerPreview() {
    val context = LocalContext.current
    QuizziTheme {
        GameRoundContent(
            gameType = "ResistanceGame",
            state =
                previewGameState(
                    selectedAnswerId = 1,
                    playerRoundResult = PlayerRoundResult(answerId = 1, isCorrect = true),
                ),
            onSubmitAnswer = {},
            imageLoader = previewImageLoader(context),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentWithWrongAnswerPreview() {
    val context = LocalContext.current
    QuizziTheme {
        GameRoundContent(
            gameType = "ResistanceGame",
            state =
                previewGameState(
                    selectedAnswerId = 1,
                    playerRoundResult = PlayerRoundResult(answerId = 1, isCorrect = false),
                ),
            onSubmitAnswer = {},
            imageLoader = previewImageLoader(context),
        )
    }
}

// Helper functions for previews
private fun previewImageLoader(context: Context): ImageLoader =
    ImageLoader
        .Builder(context)
        .build()

private fun previewGameState(
    selectedAnswerId: Int? = null,
    playerRoundResult: PlayerRoundResult? = null,
) = GameUiState.RoundOn(
    player1 =
        PlayerInRoom(
            id = "1",
            name = "Player 1",
            avatarUrl = "TODO()",
            state = PlayerState.READY,
        ),
//    player2 =
//        PlayerInRoom(
//            id = "2",
//            name = "Player 2",
//            avatarUrl = "TODO()",
//            state = PlayerState.WAIT,
//        ),
    player2 = null,
    gameBarPercentage = 0.7f,
    question =
        Question(
            countryCode = "np",
            content = "What is the capital of France?",
            options =
                listOf(
                    Option(id = 1, value = "Paris"),
                    Option(id = 2, value = "London"),
                    Option(id = 3, value = "Berlin"),
                    Option(id = 4, value = "Madrid"),
                ),
        ),
    timeRemainingInSeconds = 7,
    selectedAnswerId = selectedAnswerId,
    playerRoundResult = playerRoundResult,
)
