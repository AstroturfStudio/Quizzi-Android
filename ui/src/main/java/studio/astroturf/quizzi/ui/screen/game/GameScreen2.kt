package studio.astroturf.quizzi.ui.screen.game

import CountdownOverlay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import studio.astroturf.quizzi.domain.model.Question
import studio.astroturf.quizzi.domain.model.statemachine.GameEffect
import studio.astroturf.quizzi.domain.model.statemachine.GameState

@Composable
fun GameScreen2(
    onNavigateToRooms: () -> Unit,
    onShowError: (String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.gameEffectFlow.collect { effect ->
            when (effect) {
                is GameEffect.NavigateTo -> onNavigateToRooms()
                is GameEffect.ShowError -> onShowError(effect.message)
                is GameEffect.ShowTimeRemaining -> { /* Handle in UI state */
                }

                is GameEffect.ShowToast -> onShowToast(effect.message)
                is GameEffect.ReceiveAnswerResult -> {
                    // Show answer feedback animation
                    val message = if (effect.answerResult.correct) "Correct!" else "Wrong answer!"
                    onShowToast(message)
                }
            }
        }
    }

    GameScreenContent(
        state = gameState,
        onSubmitAnswer = viewModel::submitAnswer,
        onNavigateBack = onNavigateToRooms,
        modifier = modifier
    )
}

@Composable
private fun GameScreenContent(
    state: GameState,
    onSubmitAnswer: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameTopBar(
                timeRemaining = when (state) {
                    is GameState.RoundActive -> state.timeRemaining
                    else -> null
                },
                cursorPosition = when (state) {
                    is GameState.RoundActive -> state.cursorPosition
                    else -> 0f
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuestionContent(
                question = when (state) {
                    is GameState.RoundActive -> state.currentQuestion
                    else -> null
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is GameState.RoundActive -> {
                    AnswerGrid(
                        question = state.currentQuestion,
                        onAnswerSelected = onSubmitAnswer,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                is GameState.GameOver -> {
                    GameOverOverlay(
                        winner = state.winnerPlayerId,
                        onNavigateBack = onNavigateBack
                    )
                }

                else -> {
                    LoadingIndicator()
                }
            }
        }

        // Overlay animations
        AnimatedVisibility(
            visible = state is GameState.Initializing,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            CountdownOverlay(countdown = (state as? GameState.Initializing)?.timeRemaining ?: 0)
        }
    }
}

@Composable
private fun GameTopBar(
    timeRemaining: Long?,
    cursorPosition: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeDisplay(
            timeRemaining = timeRemaining,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        GameProgressBar(
            progress = 1f - cursorPosition,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimeDisplay(
    timeRemaining: Long?,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (timeRemaining?.let { it <= 3 } == true) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(
        shape = CircleShape,
        color = when {
            timeRemaining?.let { it <= 3 } == true -> MaterialTheme.colorScheme.error
            timeRemaining?.let { it <= 5 } == true -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.primary
        },
        modifier = modifier.scale(animatedScale)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = timeRemaining?.toString() ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun QuestionContent(
    question: Question?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = question?.content ?: "",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            AsyncImage(
                model = question?.imageUrl,
                contentDescription = "Question Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(280.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun AnswerGrid(
    question: Question,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        question.options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowOptions.forEach { option ->
                    AnswerButton(
                        text = option.value,
                        onClick = { onAnswerSelected(option.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}