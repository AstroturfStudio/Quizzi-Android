package com.alicankorkmaz.quizzi.ui.screen.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onNavigateToRooms: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.playerId) {
        if (uiState.playerId != null) {
            onNavigateToRooms()
        }
    }

    LandingScreenContent(
        modifier = modifier,
        savedPlayerId = uiState.savedPlayerId,
        error = uiState.error,
        onCreatePlayer = { name, avatarUrl ->
            viewModel.createPlayer(name, avatarUrl)
        },
        onLogin = { playerId ->
            viewModel.login(playerId)
        }
    )
}

@Composable
fun LandingScreenContent(
    modifier: Modifier = Modifier,
    savedPlayerId: String? = null,
    error: String? = null,
    onCreatePlayer: (String, String) -> Unit = { _, _ -> },
    onLogin: (String) -> Unit = { _ -> }
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (savedPlayerId != null) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = { onLogin(savedPlayerId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue Playing")
            }
        } else {
            var name by remember { mutableStateOf("") }
            var avatarUrl by remember { mutableStateOf("https://api.dicebear.com/7.x/avataaars/svg") }

            Text(
                text = "Welcome to Quizzi!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter your name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    onCreatePlayer(name, avatarUrl)
                    // Store player ID will be handled in ViewModel after successful creation
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Start Playing")
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview_NewUser() {
    MaterialTheme {
        LandingScreenContent()
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview_ExistingUser() {
    MaterialTheme {
        LandingScreenContent(
            modifier = Modifier.fillMaxSize()
        )
    }
}
