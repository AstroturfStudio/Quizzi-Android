package com.alicankorkmaz.quizzi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GameOverScreen(
    modifier: Modifier = Modifier,
    uiState: QuizUiState,
    backToLobby: () -> Unit
) {
    GameOverContent(
        score = uiState.score,
        totalQuestions = uiState.totalQuestions,
        winner = uiState.winner,
        onBackToLobby = backToLobby,
        modifier = modifier
    )
}

@Composable
private fun GameOverContent(
    score: Int,
    totalQuestions: Int,
    winner: String?,
    onBackToLobby: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oyun Bitti!",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        winner?.let {
            Text(
                text = "Kazanan: $it",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Skorunuz: $score / $totalQuestions",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackToLobby,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Lobiye Dön")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameOverContentPreview() {
    GameOverContent(
        score = 7,
        totalQuestions = 10,
        winner = "Player1",
        onBackToLobby = {},
        modifier = Modifier
    )
}