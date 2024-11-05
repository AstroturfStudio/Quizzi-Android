package com.alicankorkmaz.flagquiz.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alicankorkmaz.flagquiz.domain.model.ClientQuestion
import com.alicankorkmaz.flagquiz.domain.model.GameState
import com.alicankorkmaz.flagquiz.domain.model.Option

@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    uiState: QuizUiState,
    submitAnswer: (String) -> Unit
) {
    QuizContent(
        uiState = uiState,
        submitAnswer = submitAnswer,
        modifier = modifier
    )
}

@Composable
private fun QuizContent(
    uiState: QuizUiState,
    submitAnswer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAnswerAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (showAnswerAnimation) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.3f)
    )

    // Süre animasyonu için state
    val timeScale by animateFloatAsState(
        targetValue = if (uiState.timeRemaining?.let { it < 5 } == true) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearEasing
        )
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        GameBar(
            cursorPosition = 1f - uiState.cursorPosition,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(24.dp)
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Süre göstergesi
            Text(
                text = "${uiState.timeRemaining ?: ""}",
                style = MaterialTheme.typography.headlineMedium,
                color = when {
                    uiState.timeRemaining?.let { it <= 3 } == true -> Color.Red
                    uiState.timeRemaining?.let { it <= 5 } == true -> Color.Yellow
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .scale(timeScale)
            )

            Text(
                text = "Bu hangi ülkenin bayrağı?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bayrak ve soru alanı
            uiState.currentQuestion?.let { question ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn()
                ) {
                    AsyncImage(
                        model = question.flagUrl,
                        contentDescription = "Bayrak",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                            .scale(scale),
                        contentScale = ContentScale.Fit
                    )
                }

                // Şıklar
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally() + fadeIn()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        question.options.forEachIndexed { index, option ->
                            OptionButton(
                                text = option.name,
                                letter = ('A' + index),
                                onClick = {
                                    showAnswerAnimation = true
                                    submitAnswer(option.id)
                                }
                            )
                        }
                    }
                }
            }

            // Oyun sonu göstergesi
            if (uiState.gameState == GameState.FINISHED) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn()
                ) {
                    Text(
                        text = "Oyun Bitti!",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizContentPreview() {
    val previewQuestion = ClientQuestion(
        flagUrl = "https://example.com/flag.png",
        options = listOf(
            Option("1", "Türkiye"),
            Option("2", "Almanya"),
            Option("3", "Fransa"),
            Option("4", "İtalya")
        )
    )

    val previewUiState = QuizUiState(
        currentQuestion = previewQuestion,
        timeRemaining = 10L,
        gameState = GameState.PLAYING,
        cursorPosition = 0.5f
    )

    QuizContent(
        uiState = previewUiState,
        submitAnswer = {}
    )
}

