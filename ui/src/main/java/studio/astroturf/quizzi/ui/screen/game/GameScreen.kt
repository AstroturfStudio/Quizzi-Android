package studio.astroturf.quizzi.ui.screen.game

import CountdownOverlay
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.ui.screen.game.composables.gameover.GameOverContent
import studio.astroturf.quizzi.ui.screen.game.composables.lobby.LobbyContent
import studio.astroturf.quizzi.ui.screen.game.composables.paused.PausedContent
import studio.astroturf.quizzi.ui.screen.game.composables.round.GameRoundContent
import studio.astroturf.quizzi.ui.screen.game.composables.roundend.RoundResultOverlay
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    onNavigateToRooms: () -> Unit,
    onShowError: (String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    GameScreenContent(
        state = uiState,
        onNavigateToRooms = onNavigateToRooms,
        onSubmitAnswer = viewModel::submitAnswer,
        modifier = modifier
    )
}

@Composable
private fun GameScreenContent(
    state: GameUiState,
    onNavigateToRooms: () -> Unit,
    onSubmitAnswer: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = state.toAnimationKey(),
            transitionSpec = { getTransitionSpec(targetState, initialState) },
            modifier = Modifier.fillMaxSize()
        ) { stateKey ->
            GameStateContent(
                currentState = state,
                stateKey = stateKey,
                onNavigateToRooms = onNavigateToRooms,
                onSubmitAnswer = onSubmitAnswer
            )
        }
    }
}


@Composable
private fun GameStateContent(
    currentState: GameUiState,
    stateKey: GameStateAnimationKey,
    onNavigateToRooms: () -> Unit,
    onSubmitAnswer: (Int) -> Unit
) {
    when {
        stateKey == GameStateAnimationKey.IDLE -> LoadingIndicator()

        stateKey == GameStateAnimationKey.LOBBY && currentState is GameUiState.Lobby -> {
            LobbyContent(
                roomName = currentState.roomName,
                creator = currentState.creator,
                challenger = currentState.challenger
            )
        }

        stateKey == GameStateAnimationKey.STARTING && currentState is GameUiState.Starting -> {
            CountdownOverlay(
                countdown = currentState.timeRemainingInSeconds
            )
        }

        stateKey == GameStateAnimationKey.ROUND && currentState is GameUiState.RoundOn -> {
            GameRoundContent(
                state = currentState,
                onSubmitAnswer = onSubmitAnswer
            )
        }

        stateKey == GameStateAnimationKey.GAME_OVER && currentState is GameUiState.GameOver -> {
            GameOverContent(
                winner = currentState.winner,
                totalRounds = currentState.totalRoundCount,
                onNavigateBack = onNavigateToRooms
            )
        }

        stateKey == GameStateAnimationKey.PAUSED && currentState is GameUiState.Paused -> {
            PausedContent(
                reason = currentState.reason,
                onlinePlayers = currentState.onlinePlayers,
                onRetry = {}
            )
        }

        stateKey == GameStateAnimationKey.ROUND_END && currentState is GameUiState.RoundEnd -> {
            RoundResultOverlay(
                correctAnswerText = currentState.correctAnswerValue,
                winnerName = currentState.roundWinner?.name ?: "kimse",
                isWinner = Random.nextBoolean() // fixme
            )
        }

        else -> LoadingIndicator()
    }
}

private enum class GameStateAnimationKey {
    IDLE, LOBBY, STARTING, ROUND, GAME_OVER, PAUSED, ROUND_END
}

private fun GameUiState.toAnimationKey(): GameStateAnimationKey = when (this) {
    is GameUiState.Idle -> GameStateAnimationKey.IDLE
    is GameUiState.Lobby -> GameStateAnimationKey.LOBBY
    is GameUiState.Starting -> GameStateAnimationKey.STARTING
    is GameUiState.RoundOn -> GameStateAnimationKey.ROUND
    is GameUiState.GameOver -> GameStateAnimationKey.GAME_OVER
    is GameUiState.Paused -> GameStateAnimationKey.PAUSED
    is GameUiState.RoundEnd -> GameStateAnimationKey.ROUND_END
}

private fun getTransitionSpec(
    targetState: GameStateAnimationKey,
    initialState: GameStateAnimationKey
): ContentTransform {
    return when (targetState) {
        GameStateAnimationKey.GAME_OVER -> (slideInVertically { height -> height } + fadeIn())
            .togetherWith(slideOutVertically { height -> -height } + fadeOut())

        GameStateAnimationKey.ROUND -> when (initialState) {
            GameStateAnimationKey.STARTING -> (expandVertically() + fadeIn())
                .togetherWith(shrinkVertically() + fadeOut())

            else -> fadeIn() togetherWith fadeOut()
        }

        else -> fadeIn() togetherWith fadeOut()
    }
}