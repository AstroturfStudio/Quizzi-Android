package studio.astroturf.quizzi.ui.screen.game

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import showToast
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.model.GameFeedback
import studio.astroturf.quizzi.ui.screen.game.composables.gameover.GameOverContent
import studio.astroturf.quizzi.ui.screen.game.composables.lobby.LobbyScreen
import studio.astroturf.quizzi.ui.screen.game.composables.paused.PausedContent
import studio.astroturf.quizzi.ui.screen.game.composables.round.GameRoundContent
import timber.log.Timber

private const val TAG = "GameScreen"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    onNavigateToRooms: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val uiNotification by viewModel.notification.collectAsState()

    // Log state changes using a side effect
    LaunchedEffect(uiState) {
        Timber.tag(TAG).d("State is being changed to: $uiState")
    }

    LaunchedEffect(uiNotification) {
        when (uiNotification) {
            is UiNotification.Toast -> context.showToast((uiNotification as UiNotification.Toast).message)
            else -> {} // Handle other types of notifications if needed
        }
    }

    GameScreenContent(
        categoryId = viewModel.getCategoryId()!!,
        gameType = viewModel.getGameType()!!,
        state = uiState,
        onNavigateToRooms = onNavigateToRooms,
        onReadyToPlay = viewModel::readyToPlay,
        onSubmitAnswer = viewModel::submitAnswer,
        onSubmitFeedback = viewModel::submitFeedback,
        imageLoader = viewModel.imageLoader,
        modifier = modifier,
    )
}

@Composable
private fun GameScreenContent(
    categoryId: Int,
    gameType: String,
    state: GameUiState,
    onNavigateToRooms: () -> Unit,
    onReadyToPlay: () -> Unit,
    onSubmitAnswer: (Int) -> Unit,
    onSubmitFeedback: (GameFeedback) -> Unit,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        GameStateContent(
            categoryId = categoryId,
            gameType = gameType,
            currentState = state,
            onNavigateToRooms = onNavigateToRooms,
            onReadyToPlay = onReadyToPlay,
            onSubmitAnswer = onSubmitAnswer,
            onSubmitFeedback = onSubmitFeedback,
            imageLoader = imageLoader,
        )
    }
}

@Composable
private fun GameStateContent(
    categoryId: Int,
    gameType: String,
    currentState: GameUiState,
    onNavigateToRooms: () -> Unit,
    onReadyToPlay: () -> Unit,
    onSubmitAnswer: (Int) -> Unit,
    onSubmitFeedback: (GameFeedback) -> Unit,
    imageLoader: ImageLoader,
) {
    when {
        currentState is GameUiState.Lobby -> {
            LobbyScreen(
                lobbyUiModel = currentState.lobbyUiModel,
                onBackPress = onNavigateToRooms,
                onReadyToPlay = onReadyToPlay,
            )
        }

        currentState is GameUiState.RoundOn -> {
            GameRoundContent(
                categoryId = categoryId,
                gameType = gameType,
                state = currentState,
                onSubmitAnswer = onSubmitAnswer,
                imageLoader = imageLoader,
            )
        }

        currentState is GameUiState.GameOver -> {
            GameOverContent(
                state = currentState,
                onNavigateBack = onNavigateToRooms,
                onSubmitFeedback = onSubmitFeedback,
            )
        }

        currentState is GameUiState.Paused -> {
            PausedContent(
                reason = currentState.reason,
                onlinePlayers = currentState.onlinePlayers,
                onRetry = {},
            )
        }

        else -> LoadingIndicator()
    }
}
