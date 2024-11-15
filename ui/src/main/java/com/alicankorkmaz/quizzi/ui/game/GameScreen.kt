package com.alicankorkmaz.quizzi.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.alicankorkmaz.quizzi.domain.model.Option
import com.alicankorkmaz.quizzi.domain.model.Question
import com.alicankorkmaz.quizzi.domain.model.RoomState
import com.alicankorkmaz.quizzi.ui.components.AnswerOptionsGrid
import com.alicankorkmaz.quizzi.ui.components.GameOverOverlay
import com.alicankorkmaz.quizzi.ui.components.RoundResultOverlay

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GameScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSubmitAnswer = { viewModel.submitAnswer(it) }
    )
}

@Composable
private fun GameScreenContent(
    modifier: Modifier,
    uiState: GameUiState,
    onSubmitAnswer: (Int) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Ana oyun içeriği
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Üst bilgi çubuğu
            GameInfoBar(
                timeRemaining = uiState.timeRemaining,
                cursorPosition = uiState.cursorPosition
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Bayrak ve soru alanı
            FlagQuestionCard(
                question = uiState.currentQuestion,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cevap şıkları
            AnswerOptionsGrid(
                question = uiState.currentQuestion,
                lastAnswer = uiState.lastAnswer,
                hasAnswered = uiState.hasAnswered,
                onAnswerSelected = onSubmitAnswer,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Round sonucu overlay
        AnimatedVisibility(
            visible = uiState.showRoundResult,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            RoundResultOverlay(
                correctAnswer = uiState.correctAnswer ?: -1,
                winnerName = uiState.winnerPlayerName,
                isWinner = uiState.isWinner
            )
        }

        // Oyun sonu overlay
        AnimatedVisibility(
            visible = uiState.roomState == RoomState.FINISHED,
            enter = fadeIn() + slideInVertically(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(3f)
        ) {
            GameOverOverlay(
                winner = uiState.winner,
                score = uiState.score,
                totalQuestions = uiState.totalQuestions
            )
        }
    }
}

@Composable
private fun GameInfoBar(
    timeRemaining: Long?,
    cursorPosition: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeCounter(timeRemaining = timeRemaining)

        Spacer(modifier = Modifier.width(16.dp))

        GameProgressBar(
            progress = 1f - cursorPosition,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimeCounter(
    timeRemaining: Long?,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (timeRemaining?.let { it <= 3 } == true) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(
        shape = CircleShape,
        color = when {
            timeRemaining?.let { it <= 3 } == true -> Color.Red
            timeRemaining?.let { it <= 5 } == true -> Color(0xFFFFA000)
            else -> MaterialTheme.colorScheme.primary
        },
        modifier = modifier
            .size(56.dp)
            .scale(scale)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "${timeRemaining ?: ""}",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun GameProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun FlagQuestionCard(
    question: Question?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = question?.content ?: "",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            AsyncImage(
                model = question?.imageUrl,
                contentDescription = "Bayrak",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(280.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenContentPreview() {
    val previewQuestion = Question(
        imageUrl = "https://example.com/flag.png",
        content = "conceptam",
        options = listOf(
            Option(1, "Türkiye"),
            Option(2, "Almanya"),
            Option(3, "Fransa"),
            Option(4, "İtalya")
        )
    )

    val previewUiState = GameUiState(
        currentQuestion = previewQuestion,
        timeRemaining = 10L,
        roomState = RoomState.PLAYING,
        cursorPosition = 0.5f,
        showRoundResult = true,
        correctAnswer = 1,
        winnerPlayerName = "Oyuncu 1",
        isWinner = true,
        score = 5,
        totalQuestions = 10
    )

    GameScreenContent(
        modifier = Modifier.fillMaxSize(),
        uiState = previewUiState,
        onSubmitAnswer = {}
    )
}

@Preview
@Composable
private fun GameInfoBarPreview() {
    GameInfoBar(
        timeRemaining = 5L,
        cursorPosition = 0.7f
    )
}

@Preview
@Composable
private fun TimeCounterPreview() {
    TimeCounter(timeRemaining = 3L)
}

@Preview
@Composable
private fun FlagQuestionCardPreview() {
    FlagQuestionCard(
        question = Question(
            imageUrl = "https://example.com/flag.png",
            content = "Hangi ülkedir?",
            options = listOf(
                Option(1, "Türkiye"),
                Option(2, "Almanya"),
                Option(3, "Fransa"),
                Option(4, "İtalya")
            )
        )
    )
}

