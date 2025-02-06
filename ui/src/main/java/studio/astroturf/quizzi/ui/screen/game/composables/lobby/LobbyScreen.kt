package studio.astroturf.quizzi.ui.screen.game.composables.lobby

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.AppBarScreen
import studio.astroturf.quizzi.ui.components.ClickableIcon
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.BodyNormalRegular
import studio.astroturf.quizzi.ui.theme.BodySmallMedium
import studio.astroturf.quizzi.ui.theme.Grey2
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.Heading3
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.White

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun LobbyScreen(onBackPress: (() -> Unit)? = null) {
    AppBarScreen(
        title = null,
        leadingIcon =
            ClickableIcon(
                iconResId = R.drawable.ic_back,
                onClick = onBackPress,
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 24.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.illustration_lobby),
                contentDescription = null,
                modifier =
                    Modifier
                        .height(200.dp)
                        .align(Alignment.End),
            )

            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .background(color = White, shape = RoundedCornerShape(32.dp))
                        .padding(horizontal = 16.dp, vertical = 24.dp),
            ) {
                Text(
                    text = "SPORTS",
                    style = BodySmallMedium.copy(color = Grey2),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Guven’s Room",
                    style = Heading3.copy(color = Black),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    Modifier
                        .width(327.dp)
                        .height(66.79365.dp)
                        .background(
                            color = Grey5,
                            shape = RoundedCornerShape(size = 20.dp),
                        ),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "DESCRIPTION",
                    style = BodySmallMedium.copy(color = Grey2),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text =
                        "Try to answer the questions before the other player. " +
                            "Whoever answers first breaks the other’s defense. " +
                            "Show resilience against your opponent!",
                    style = BodyNormalRegular.copy(color = Black),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    Modifier
                        .size(40.dp)
                        .background(
                            color = Grey5,
                            shape = CircleShape,
                        ),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    Modifier
                        .size(40.dp)
                        .background(
                            color = Grey5,
                            shape = CircleShape,
                        ),
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(color = Primary, shape = RoundedCornerShape(size = 20.dp)),
                    onClick = {
                    },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = Primary),
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = "Ready To Play",
                        style = BodyNormalMedium.copy(color = White),
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun LobbyScreenPreview() {
    QuizziTheme {
        LobbyScreen()
    }
}
