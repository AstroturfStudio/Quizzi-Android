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
import studio.astroturf.quizzi.ui.theme.BodyLargeMedium
import studio.astroturf.quizzi.ui.theme.BodyXLarge
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.White

@Composable
fun GameRoundContent(
    categoryId: Int,
    gameType: String,
    state: GameUiState.RoundOn,
    onSubmitAnswer: (Int) -> Unit,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // More aggressive adjustments for very small screens
    val isVerySmallScreen = screenHeight < 600
    val isSmallScreen = screenHeight < 720 && !isVerySmallScreen

    // Adjust padding and spacing based on screen size
    val outerPadding =
        when {
            isSmallScreen or isVerySmallScreen -> 4.dp
            else -> 8.dp
        }

    val horizontalPadding = 16.dp

    val verticalPadding =
        when {
            isSmallScreen or isVerySmallScreen -> 24.dp
            else -> 36.dp
        }

    val cornerRadius =
        when {
            isSmallScreen or isVerySmallScreen -> 24.dp
            else -> 32.dp
        }

    // Adjust spacer heights based on screen size
    val spacerHeightSmall =
        when {
            isSmallScreen or isVerySmallScreen -> 8.dp
            else -> 16.dp
        }

    val spacerHeightMedium =
        when {
            isSmallScreen or isVerySmallScreen -> 12.dp
            else -> 22.dp
        }

    val spacerHeightLarge =
        when {
            isSmallScreen or isVerySmallScreen -> 36.dp
            else -> 48.dp
        }

    val imageHeight =
        when {
            isVerySmallScreen -> 120.dp
            isSmallScreen -> 180.dp
            else -> 240.dp
        }

    // Adjust question box height
    val questionBoxHeight =
        when {
            isSmallScreen or isVerySmallScreen -> 48.dp
            else -> 64.dp
        }

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
                    categoryId = categoryId,
                    imageCode = it,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(imageHeight),
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
                    style = if (isVerySmallScreen || isSmallScreen) BodyLargeMedium else BodyXLarge,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(spacerHeightMedium))

            if (state.player2 != null) {
                VersusDisplay(
                    player1 = state.player1,
                    player2 = state.player2,
                    timeRemainingInSeconds = state.timeRemainingInSeconds,
                    imageLoader = imageLoader,
                    isSmallScreen = isVerySmallScreen || isSmallScreen,
                )
            } else {
                TimeDisplay(
                    totalTime = getTotalTime(gameType),
                    timeLeft = state.timeRemainingInSeconds,
                    modifier = Modifier.wrapContentSize(),
                    isSmallScreen = isVerySmallScreen || isSmallScreen,
                )
            }

            Spacer(modifier = Modifier.height(spacerHeightSmall))

            GameBar(
                modifier =
                    Modifier
                        .padding(
                            horizontal =
                                when {
                                    isSmallScreen or isVerySmallScreen -> 24.dp
                                    else -> 36.dp
                                },
                        ).fillMaxWidth()
                        .height(
                            when {
                                isSmallScreen or isVerySmallScreen -> 6.dp
                                else -> 8.dp
                            },
                        ),
                cursorPosition = 1 - state.gameBarPercentage,
            )

            Spacer(modifier = Modifier.height(spacerHeightLarge))

            // Give more weight to the answer grid on small screens
            Box(
                modifier =
                    Modifier
                        .weight(if (isVerySmallScreen) 1.2f else 1f, fill = false),
                contentAlignment = Alignment.Center,
            ) {
                AnswerGrid(
                    modifier =
                        Modifier
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
        "Resistance Game" -> 10
        "Resist To Time Game" -> 3
        else -> 10
    }

@Composable
private fun VersusDisplay(
    player1: PlayerInRoom,
    player2: PlayerInRoom,
    timeRemainingInSeconds: Int,
    imageLoader: ImageLoader,
    isSmallScreen: Boolean = false,
) {
    Row(
        modifier =
            Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 48.dp else 56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerDisplay(
            player = player1,
            imageLoader = imageLoader,
            isSmallScreen = isSmallScreen,
        )

        TimeDisplay(
            totalTime = 10,
            timeLeft = timeRemainingInSeconds,
            modifier = Modifier.wrapContentSize(),
            isSmallScreen = isSmallScreen,
        )

        PlayerDisplay(
            player = player2,
            imageLoader = imageLoader,
            isSmallScreen = isSmallScreen,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameRoundContentPreview() {
    val context = LocalContext.current
    QuizziTheme {
        GameRoundContent(
            1,
            gameType = "Resistance Game",
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
            1,
            gameType = "Resistance Game",
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
            1,
            gameType = "Resistance Game",
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
            categoryId = 1,
            gameType = "Resistance Game",
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
