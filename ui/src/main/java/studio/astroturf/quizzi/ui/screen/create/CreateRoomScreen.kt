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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import isNotNullOrBlank
import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.domain.model.GameType
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.AppBarScreen
import studio.astroturf.quizzi.ui.components.ClickableIcon
import studio.astroturf.quizzi.ui.navigation.QuizziNavDestination
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
    navController: NavController,
    onBackPress: () -> Unit,
    onCategoryClick: () -> Unit,
    onGameTypeClick: () -> Unit,
    viewModel: CreateRoomViewModel = hiltViewModel(),
    onCreateRoom: (String, Category, GameType) -> Unit,
) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val roomTitle by viewModel.roomTitle.collectAsState()
    val quizCategory by viewModel.quizCategory.collectAsState()
    val gameType by viewModel.gameType.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setQuizCategory(savedStateHandle?.get<Category>(QuizziNavDestination.CreateRoom.ARG_SELECTED_QUIZ_CATEGORY))
        viewModel.setGameType(savedStateHandle?.get<GameType>(QuizziNavDestination.CreateRoom.ARG_SELECTED_GAME_TYPE))
    }

    CreateRoomScreenContent(
        onBackPress = onBackPress,
        roomTitle = roomTitle,
        quizCategory = quizCategory,
        gameType = gameType,
        onRoomTitleChange = viewModel::setRoomTitle,
        onCategoryClick = onCategoryClick,
        onGameTypeClick = onGameTypeClick,
        onCreateRoom = onCreateRoom,
    )
}

@Composable
private fun CreateRoomScreenContent(
    onBackPress: () -> Unit,
    roomTitle: String?,
    quizCategory: Category?,
    gameType: GameType?,
    onRoomTitleChange: (String) -> Unit,
    onCategoryClick: () -> Unit,
    onGameTypeClick: () -> Unit,
    onCreateRoom: (String, Category, GameType) -> Unit,
) {
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
                    ).padding(16.dp)
                    .imePadding(),
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
                    value = roomTitle ?: "",
                    onValueChange = onRoomTitleChange,
                    textStyle =
                        BodyNormalRegular.copy(
                            color = Black,
                            textAlign = TextAlign.Start,
                            textDecoration = TextDecoration.None,
                        ),
                    decorationBox = { innerTextField ->

                        Box(contentAlignment = Alignment.CenterStart) {
                            if (roomTitle.isNullOrEmpty()) {
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
                        text = quizCategory?.name ?: "Choose quiz category",
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
                        text = gameType?.name ?: "Choose game type",
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

                val isButtonEnabled =
                    roomTitle.isNotNullOrBlank() && quizCategory != null && gameType != null

                Button(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                color = if (isButtonEnabled) Primary else Grey2,
                                shape = RoundedCornerShape(size = 20.dp),
                            ),
                    onClick = {
                        onCreateRoom(roomTitle!!, quizCategory!!, gameType!!)
                    },
                    colors =
                        ButtonDefaults
                            .buttonColors()
                            .copy(containerColor = Primary, disabledContainerColor = Grey2),
                    enabled = isButtonEnabled,
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = "Create Room",
                        style = BodyNormalMedium.copy(color = White),
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
        CreateRoomScreenContent(
            onBackPress = {},
            roomTitle = "Room Title",
            onCategoryClick = {},
            quizCategory = Category(0, "Category"),
            onGameTypeClick = {},
            gameType = GameType("Resistence Game"),
            onCreateRoom = { _, _, _ -> },
            onRoomTitleChange = {},
        )
    }
}
