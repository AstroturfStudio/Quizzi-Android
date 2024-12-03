package studio.astroturf.quizzi.ui.screen.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.ui.screen.game.composables.gameover.GameOverContent
import studio.astroturf.quizzi.ui.screen.game.composables.lobby.LobbyContent
import studio.astroturf.quizzi.ui.screen.game.composables.paused.PausedContent
import studio.astroturf.quizzi.ui.screen.game.composables.round.GameRoundContent
import studio.astroturf.quizzi.ui.screen.game.composables.roundend.RoundResultOverlay
import timber.log.Timber

private const val TAG = "GameScreen"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    onNavigateToRooms: () -> Unit,
    onShowError: (String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Log state changes using a side effect
    LaunchedEffect(uiState) {
        Timber.tag(TAG).d("State changed to: ${uiState::class.simpleName}")
        when (uiState) {
            is GameUiState.RoundEnd -> {
                val roundEnd = uiState as GameUiState.RoundEnd
                Timber.tag(TAG).d(
                    "%snull",
                    "RoundEnd details - Winner: ${roundEnd.roundWinner.winnerName}, " +
                        "Correct Answer: ${roundEnd.correctAnswerValue}, ",
                )
            }
            is GameUiState.RoundOn -> {
                val roundOn = uiState as GameUiState.RoundOn
                Timber.tag(TAG).d(
                    "%snull",
                    "RoundOn details - Question: ${roundOn.question.content}, " +
                        "Time: ${roundOn.timeRemainingInSeconds}, ",
                )
            }
            else -> {
                // Log additional details for other states if needed
                Timber.tag(TAG).d("State details: $uiState")
            }
        }
    }

    GameScreenContent(
        state = uiState,
        onNavigateToRooms = onNavigateToRooms,
        onSubmitAnswer = viewModel::submitAnswer,
        modifier = modifier,
    )
}

@Composable
private fun GameScreenContent(
    state: GameUiState,
    onNavigateToRooms: () -> Unit,
    onSubmitAnswer: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Log animation state changes
        val currentAnimationKey = state.toAnimationKey()
        LaunchedEffect(currentAnimationKey) {
            Timber.tag(TAG).d("Animation state changed to: $currentAnimationKey")
        }

        AnimatedContent(
            targetState = currentAnimationKey,
            transitionSpec = {
                val spec = getTransitionSpec(targetState)
                // Log transition information
                Timber.tag(TAG).d("Transition from $initialState to $targetState")
                spec
            },
            modifier = Modifier.fillMaxSize(),
        ) { stateKey ->
            // Log content state changes
            LaunchedEffect(stateKey) {
                Timber.tag(TAG).d("Rendering content for state: $stateKey")
            }

            GameStateContent(
                currentState = state,
                stateKey = stateKey,
                onNavigateToRooms = onNavigateToRooms,
                onSubmitAnswer = onSubmitAnswer,
            )
        }
    }
}

@Composable
private fun GameStateContent(
    currentState: GameUiState,
    stateKey: GameStateAnimationKey,
    onNavigateToRooms: () -> Unit,
    onSubmitAnswer: (Int) -> Unit,
) {
    when {
        stateKey == GameStateAnimationKey.IDLE -> LoadingIndicator()

        stateKey == GameStateAnimationKey.LOBBY && currentState is GameUiState.Lobby -> {
            LobbyContent(
                roomName = currentState.roomName,
                creator = currentState.creator,
                challenger = currentState.challenger,
                countdown = currentState.countdown?.timeRemainingInSeconds,
            )
        }

        stateKey == GameStateAnimationKey.ROUND && currentState is GameUiState.RoundOn -> {
            GameRoundContent(
                state = currentState,
                onSubmitAnswer = onSubmitAnswer,
            )
        }

        stateKey == GameStateAnimationKey.GAME_OVER && currentState is GameUiState.GameOver -> {
            if (currentState.winner != null && currentState.totalRoundCount != null) {
                GameOverContent(
                    winner = currentState.winner,
                    totalRounds = currentState.totalRoundCount,
                    onNavigateBack = onNavigateToRooms,
                )
            }
        }

        stateKey == GameStateAnimationKey.PAUSED && currentState is GameUiState.Paused -> {
            PausedContent(
                reason = currentState.reason,
                onlinePlayers = currentState.onlinePlayers,
                onRetry = {},
            )
        }

        stateKey == GameStateAnimationKey.ROUND_END && currentState is GameUiState.RoundEnd -> {
            RoundResultOverlay(
                correctAnswerText = currentState.correctAnswerValue,
                roundWinner = currentState.roundWinner,
            )
        }

        else -> LoadingIndicator()
    }
}

private enum class GameStateAnimationKey {
    IDLE,
    LOBBY,
    ROUND,
    GAME_OVER,
    PAUSED,
    ROUND_END,
}

private fun GameUiState.toAnimationKey(): GameStateAnimationKey =
    when (this) {
        is GameUiState.Idle -> GameStateAnimationKey.IDLE
        is GameUiState.Lobby -> GameStateAnimationKey.LOBBY
        is GameUiState.RoundOn -> GameStateAnimationKey.ROUND
        is GameUiState.GameOver -> GameStateAnimationKey.GAME_OVER
        is GameUiState.Paused -> GameStateAnimationKey.PAUSED
        is GameUiState.RoundEnd -> GameStateAnimationKey.ROUND_END
    }

private fun getTransitionSpec(targetState: GameStateAnimationKey): ContentTransform =
    when (targetState) {
        GameStateAnimationKey.GAME_OVER ->
            (slideInVertically { height -> height } + fadeIn())
                .togetherWith(slideOutVertically { height -> -height } + fadeOut())

        else -> fadeIn() togetherWith fadeOut()
    }
