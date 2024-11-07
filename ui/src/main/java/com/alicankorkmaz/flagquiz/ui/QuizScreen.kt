package com.alicankorkmaz.flagquiz.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.alicankorkmaz.flagquiz.domain.model.ClientQuestion
import com.alicankorkmaz.flagquiz.domain.model.GameState
import com.alicankorkmaz.flagquiz.domain.model.Option
import com.alicankorkmaz.flagquiz.ui.components.RoundResultOverlay

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

            // Bayrak için arka plan ekleyelim
            AsyncImage(
                model = uiState.currentQuestion?.flagUrl,
                contentDescription = "Bayrak",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )

            // Şıklar için yeni bir Composable kullanalım
            uiState.currentQuestion?.let { question ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally() + fadeIn(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // İlk satır (A ve B şıkları)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            question.options.take(2).forEachIndexed { index, option ->
                                AnswerOptionButton(
                                    text = option.name,
                                    letter = ('A' + index),
                                    isSelected = uiState.lastAnswer?.answer == option.id,
                                    isCorrect = uiState.lastAnswer?.let { it.correct && it.answer == option.id } == true,
                                    enabled = !uiState.hasAnswered,
                                    onClick = { submitAnswer(option.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // İkinci satır (C ve D şıkları)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            question.options.drop(2).forEachIndexed { index, option ->
                                AnswerOptionButton(
                                    text = option.name,
                                    letter = ('C' + index),
                                    isSelected = uiState.lastAnswer?.answer == option.id,
                                    isCorrect = uiState.lastAnswer?.let { it.correct && it.answer == option.id } == true,
                                    enabled = !uiState.hasAnswered,
                                    onClick = { submitAnswer(option.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
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

    // Round sonucu overlay'i
    if (uiState.showRoundResult) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)  // Overlay'i diğer içeriğin üzerine çıkar
        ) {
            RoundResultOverlay(
                correctAnswer = uiState.correctAnswer ?: "",
                winnerName = uiState.winnerPlayerName,
                isWinner = uiState.isWinner,
                modifier = Modifier.fillMaxSize()
            )
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

