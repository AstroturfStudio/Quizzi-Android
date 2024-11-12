package com.alicankorkmaz.quizzi.ui

import LobbyUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
fun WaitingRoomScreen(
    modifier: Modifier = Modifier,
    lobbyState: LobbyUiState,
    onBackToLobby: () -> Unit
) {
    WaitingRoomContent(
        roomId = lobbyState.currentRoom ?: "",
        players = lobbyState.players,
        onBackToLobby = onBackToLobby,
        modifier = modifier
    )
}

@Composable
private fun WaitingRoomContent(
    roomId: String,
    players: List<String>,
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
            text = "Oda Kodu: $roomId",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Oyuncular",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        players.forEach { player ->
            Text(
                text = player,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Button(
            onClick = onBackToLobby,
            modifier = Modifier
                .padding(top = 32.dp)
        ) {
            Text("Lobiye Dön")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WaitingRoomScreenPreview() {
    WaitingRoomScreen(
        lobbyState = LobbyUiState(
            currentRoom = "ABC123",
            players = listOf("Alice", "Bob", "Charlie")
        ),
        onBackToLobby = {}
    )
}

