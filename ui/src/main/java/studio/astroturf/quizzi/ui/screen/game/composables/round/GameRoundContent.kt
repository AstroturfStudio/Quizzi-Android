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
import androidx.compose.ui.platform.LocalConfiguration
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
import studio.astroturf.quizzi.ui.theme.BodyLarge
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    
    // Adjust padding and spacing based on screen size
    val outerPadding = if (screenHeight < 600) 4.dp else 8.dp
    val horizontalPadding = if (screenHeight < 600) 12.dp else 16.dp
    val verticalPadding = if (screenHeight < 600) 24.dp else 36.dp
    val cornerRadius = if (screenHeight < 600) 24.dp else 32.dp
    
    // Adjust spacer heights based on screen size
    val spacerHeightSmall = if (screenHeight < 600) 8.dp else 16.dp
    val spacerHeightMedium = if (screenHeight < 600) 12.dp else 22.dp
    val spacerHeightLarge = if (screenHeight < 600) 24.dp else 48.dp
    
    // Adjust image size based on screen size
    val imageWidth = if (screenHeight < 600) 240.dp else 320.dp
    val imageHeight = if (screenHeight < 600) 120.dp else 160.dp
    
    // Adjust question box height
    val questionBoxHeight = if (screenHeight < 600) 48.dp else 64.dp
    
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = Primary)
                .padding(outerPadding),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = White, shape = RoundedCornerShape(cornerRadius))
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    .padding(bottom = outerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.question.countryCode?.let {
                CachedQuestionImage(
                    countryCode = it,
                    modifier = Modifier.width(imageWidth).height(imageHeight),
                )
            }

            Box(
                modifier =
                    Modifier
                        .height(questionBoxHeight)
                        .fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = state.question.content,
                    style = if (screenHeight < 600) BodyLarge else BodyXLarge,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(spacerHeightMedium))

            if (state.player2 != null) {
                VersusDisplay(state.player1, state.player2, state.timeRemainingInSeconds, imageLoader)
            } else {
                TimeDisplay(
                    totalTime = getTotalTime(gameType),
                    timeLeft = state.timeRemainingInSeconds,
                    modifier = Modifier.wrapContentSize(),
                )
            }

            Spacer(modifier = Modifier.height(spacerHeightSmall))

            GameBar(
                modifier =
                    Modifier
                        .padding(horizontal = if (screenHeight < 600) 24.dp else 36.dp)
                        .fillMaxWidth()
                        .height(if (screenHeight < 600) 6.dp else 8.dp),
                cursorPosition = 1 - state.gameBarPercentage,
            )

            Spacer(modifier = Modifier.height(spacerHeightLarge))

            Box(
                modifier = Modifier
                    .weight(1f, fill = false),
                contentAlignment = Alignment.Center,
            ) {
                AnswerGrid(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
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
