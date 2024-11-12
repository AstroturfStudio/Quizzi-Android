package com.alicankorkmaz.quizzi.ui

import LobbyUiState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alicankorkmaz.quizzi.domain.model.ClientQuestion
import com.alicankorkmaz.quizzi.domain.model.GameState
import com.alicankorkmaz.quizzi.domain.model.Option

@Composable
fun HomeScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val lobbyState by viewModel.lobbyState.collectAsState()

    HomeContent(
        uiState = uiState,
        lobbyState = lobbyState,
        onCreateRoom = viewModel::createRoom,
        onJoinRoom = viewModel::joinRoom,
        onBackToLobby = viewModel::backToLobby,
        onSubmitAnswer = viewModel::submitAnswer,
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    uiState: QuizUiState,
    lobbyState: LobbyUiState,
    onCreateRoom: (String) -> Unit,
    onJoinRoom: (String, String) -> Unit,
    onBackToLobby: () -> Unit,
    onSubmitAnswer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            lobbyState.currentRoom == null -> {
                LobbyScreen(
                    lobbyState = lobbyState,
                    createRoom = onCreateRoom,
                    joinRoom = onJoinRoom
                )
            }

            uiState.gameState == null || uiState.gameState == GameState.WAITING -> {
                WaitingRoomScreen(
                    lobbyState = lobbyState,
                    onBackToLobby = onBackToLobby
                )
            }

            uiState.gameState == GameState.FINISHED -> {
                GameOverScreen(
                    uiState = uiState,
                    backToLobby = onBackToLobby
                )
            }

            else -> {
                QuizScreen(
                    uiState = uiState,
                    submitAnswer = onSubmitAnswer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    HomeContent(
        uiState = QuizUiState(
            currentQuestion = ClientQuestion(
                flagUrl = "https://example.com/flag.png",
                options = listOf(
                    Option("1", "Türkiye"),
                    Option("2", "Almanya"),
                    Option("3", "Fransa"),
                    Option("4", "İtalya")
                )
            ),
            gameState = GameState.PLAYING,
            score = 0,
            totalQuestions = 10,
            cursorPosition = 0.5f
        ),
        lobbyState = LobbyUiState(
            isConnected = true,
            currentRoom = null,
            players = listOf("Player1", "Player2")
        ),
        onCreateRoom = {},
        onJoinRoom = { _, _ -> },
        onBackToLobby = {},
        onSubmitAnswer = {}
    )
}
