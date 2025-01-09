package studio.astroturf.quizzi.ui.screen.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.screen.game.LoadingIndicator
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.BodyNormalRegular
import studio.astroturf.quizzi.ui.theme.Grey2
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.Heading3
import studio.astroturf.quizzi.ui.theme.Logo
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.White

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onNavigateToRooms: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.playerId) {
        if (uiState.playerId != null) {
            onNavigateToRooms()
        }
    }

    if (uiState.playerId == null) {
        LandingScreenContent(
            modifier = modifier,
            error = uiState.error,
            onCreatePlayer = { name, avatarUrl ->
                viewModel.createPlayer(name, avatarUrl)
            },
        )
    } else {
        LoadingIndicator()
    }
}

@Composable
fun LandingScreenContent(
    modifier: Modifier = Modifier,
    error: String? = null,
    onCreatePlayer: (String, String) -> Unit = { _, _ -> },
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = Primary)
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var name by remember { mutableStateOf("") }
        val avatarUrl by remember { mutableStateOf("https://api.dicebear.com/7.x/avataaars/svg") }

        LogoWithText()

        Image(
            painter = painterResource(R.drawable.illustration_login),
            contentDescription = "Login Illustration",
            modifier =
                Modifier
                    .padding(top = 10.dp)
                    .weight(1f),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(size = 20.dp))
                    .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier =
                        Modifier
                            .height(36.dp)
                            .fillMaxWidth(),
                    text = "Login",
                    color = Color(0xFF001833),
                    style = Heading3.copy(textAlign = TextAlign.Center),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    modifier =
                        Modifier
                            .height(24.dp)
                            .fillMaxWidth(),
                    text = "Enter your username and join us.",
                    style = BodyNormalRegular.copy(textAlign = TextAlign.Center),
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier =
                        Modifier
                            .border(
                                width = 2.dp,
                                color = Grey5,
                                shape = RoundedCornerShape(size = 20.dp),
                            ).fillMaxWidth()
                            .height(56.dp)
                            .background(color = White, shape = RoundedCornerShape(size = 20.dp))
                            .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Person icon",
                        contentScale = ContentScale.None,
                    )

                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        textStyle = BodyNormalRegular.copy(color = Black),
                        decorationBox = { innerTextField ->
                            Box {
                                if (name.isEmpty()) {
                                    Text(
                                        text = "Your username",
                                        style = BodyNormalRegular.copy(color = Grey2),
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(21.dp))

                Button(
                    onClick = {
                        onCreatePlayer(name, avatarUrl)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(color = Primary, shape = RoundedCornerShape(size = 20.dp)),
                    enabled = name.isNotBlank(),
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = "Login",
                        style = BodyNormalMedium.copy(color = White),
                    )
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = { Text("Enter your name") },
//            modifier =
//                Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//        )
//
//        Button(
//            onClick = {
//                onCreatePlayer(name, avatarUrl)
//                // Store player ID will be handled in ViewModel after successful creation
//            },
//            modifier =
//                Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//            enabled = name.isNotBlank(),
//        ) {
//            Text("Start Play")
//        }
//
//        if (error != null) {
//            Text(
//                text = error,
//                color = MaterialTheme.colorScheme.error,
//                modifier = Modifier.padding(top = 8.dp),
//            )
//        }
    }
}

@Composable
fun LogoWithText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(painter = painterResource(R.drawable.quizzi), contentDescription = "Quizzi Logo")
        Text(text = "Quizzi", style = Logo.copy(color = Color.White))
    }
}

@Preview(showBackground = true)
@Composable
private fun LandingScreenPreview() {
    MaterialTheme {
        LandingScreenContent()
    }
}
