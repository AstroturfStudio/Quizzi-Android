package studio.astroturf.quizzi.ui.screen.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.AppBarScreen
import studio.astroturf.quizzi.ui.components.ClickableIcon
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.BodyNormalRegular
import studio.astroturf.quizzi.ui.theme.Grey2
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.White

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun CreateRoomScreen(
    onBackPress: () -> Unit,
    onCategoryClick: () -> Unit,
    onGameTypeClick: () -> Unit,
    onCreateRoom: () -> Unit,
) {
    var roomTitle by remember { mutableStateOf("") }
    var quizCategory by remember { mutableStateOf("Choose quiz category") }
    var gameType by remember { mutableStateOf("Choose game type") }

    AppBarScreen(
        title = "Create Room",
        leadingIcon =
            ClickableIcon(
                iconResId = R.drawable.ic_back,
                onClick = onBackPress,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(32.dp),
                    ).padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = "Title",
                    style = BodyNormalMedium.copy(color = Black),
                )

                Spacer(Modifier.height(8.dp))

                BasicTextField(
                    value = roomTitle,
                    onValueChange = { roomTitle = it },
                    textStyle =
                        BodyNormalRegular.copy(
                            color = Black,
                            textAlign = TextAlign.Start,
                            textDecoration = TextDecoration.None,
                        ),
                    decorationBox = { innerTextField ->

                        Box(contentAlignment = Alignment.CenterStart) {
                            if (roomTitle.isEmpty()) {
                                Text(
                                    modifier = Modifier.wrapContentSize(),
                                    text = "Enter room title",
                                    style = BodyNormalRegular.copy(color = Grey2),
                                    textAlign = TextAlign.Center,
                                )
                            } else {
                                innerTextField()
                            }
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 2.dp,
                                color = Grey5,
                                shape = RoundedCornerShape(20.dp),
                            ).padding(horizontal = 24.dp, vertical = 16.dp),
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Quiz Category",
                    style = BodyNormalMedium.copy(color = Black),
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 2.dp,
                                color = Grey5,
                                shape = RoundedCornerShape(20.dp),
                            ).clickable { onCategoryClick() }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = quizCategory,
                        style = BodyNormalRegular.copy(color = Grey2),
                        textAlign = TextAlign.Center,
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Black,
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Game Type",
                    style = BodyNormalMedium.copy(color = Black),
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 2.dp,
                                color = Grey5,
                                shape = RoundedCornerShape(20.dp),
                            ).clickable { onGameTypeClick() }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = gameType,
                        style = BodyNormalRegular.copy(color = Grey2),
                        textAlign = TextAlign.Center,
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Black,
                    )
                }

                Spacer(Modifier.weight(1f))

                Button(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(color = Primary, shape = RoundedCornerShape(size = 20.dp)),
                    onClick = {
                        onCreateRoom()
                    },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = Primary),
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = "Create Room",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateRoomPreview() {
    QuizziTheme {
        CreateRoomScreen(
            onBackPress = {
            },
            onCategoryClick = {},
            onGameTypeClick = {},
            onCreateRoom = {
            },
        )
    }
}
