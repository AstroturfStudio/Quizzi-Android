package com.alicankorkmaz.quizzi.ui

import LobbyUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    lobbyState: LobbyUiState,
    createRoom: () -> Unit,
    joinRoom: (String) -> Unit,
) {

    LobbyContent(
        lobbyState = lobbyState,
        createRoom = createRoom,
        joinRoom = joinRoom,
        modifier = modifier
    )
}

@Composable
private fun LobbyContent(
    lobbyState: LobbyUiState,
    createRoom: () -> Unit,
    joinRoom: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var playerName by remember { mutableStateOf("") }
    var roomId by remember { mutableStateOf("") }
    var showJoinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bayrak Yarışması",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("İsminiz") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = { createRoom() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = playerName.isNotBlank()
        ) {
            Text("Oda Oluştur")
        }

        Button(
            onClick = { showJoinDialog = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = playerName.isNotBlank()
        ) {
            Text("Odaya Katıl")
        }

        if (lobbyState.error != null) {
            Text(
                text = lobbyState.error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Odaya Katıl") },
            text = {
                OutlinedTextField(
                    value = roomId,
                    onValueChange = { roomId = it },
                    label = { Text("Oda Kodu") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        joinRoom(roomId)
                        showJoinDialog = false
                    },
                    enabled = roomId.isNotBlank() && playerName.isNotBlank()
                ) {
                    Text("Katıl")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LobbyScreenPreview() {
    LobbyScreen(
        lobbyState = LobbyUiState(
            isConnected = true,
            rooms = listOf("Room1", "Room2"),
            error = null,
            currentRoom = null,
            players = listOf("Player1", "Player2")
        ),
        createRoom = { },
        joinRoom = { _ -> }
    )
}