package studio.astroturf.quizzi.ui.screen.create.gametype

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.domain.model.GameType
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.AppBarScreen
import studio.astroturf.quizzi.ui.components.ClickableIcon
import studio.astroturf.quizzi.ui.components.QButton
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.Secondary
import studio.astroturf.quizzi.ui.theme.White

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun GameTypeSelectionScreen(
    onBackPress: () -> Unit,
    onProceed: (GameType?) -> Unit,
    viewModel: GameTypeViewModel = hiltViewModel(),
) {
    val gameTypes by viewModel.gameTypesUiModel.collectAsState()

    AppBarScreen(
        title = stringResource(R.string.choose_game_type),
        leadingIcon =
            ClickableIcon(
                iconResId = R.drawable.ic_back,
                onClick = { onBackPress() },
            ),
    ) {
        GameTypeSelectionContent(
            gameTypes = gameTypes,
            onGameTypeClick = { viewModel.selectGameType(it) },
            onNextClick = { onProceed(viewModel.getSelectedGameType()) },
        )
    }
}

@Composable
private fun GameTypeSelectionContent(
    gameTypes: List<GameTypeUiModel>,
    onGameTypeClick: (GameTypeUiModel) -> Unit = {},
    onNextClick: () -> Unit = {},
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            gameTypes.forEach {
                item {
                    Column(
                        modifier =
                            Modifier
                                .height(132.dp)
                                .fillMaxWidth()
                                .background(
                                    color = if (it.isSelected) Secondary else Grey5,
                                    shape = RoundedCornerShape(size = 20.dp),
                                ).clickable { onGameTypeClick(it) }
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(48.dp)
                                    .background(
                                        color = if (it.isSelected) White.copy(alpha = 0.2f) else White,
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            val iconRes =
                                when (it.gameType.name) {
                                    "Resistance Game" -> R.drawable.ic_resistance_game
                                    "Resist To Time Game" -> R.drawable.ic_resist_to_time_game
                                    else -> R.drawable.ic_math
                                }
                            Icon(
                                painter = painterResource(id = iconRes),
                                tint = if (it.isSelected) White else Secondary,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }

                        Text(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(align = Alignment.Center),
                            text = it.gameType.name,
                            style = BodyNormalMedium.copy(color = if (it.isSelected) White else Secondary, textAlign = TextAlign.Center),
                            maxLines = 2,
                        )
                    }
                }
            }
        }

        QButton.Secondary(
            text = stringResource(R.string.next),
            modifier = Modifier.align(Alignment.BottomCenter),
            enabled = gameTypes.any { it.isSelected },
            onClick = onNextClick,
        )
    }
}

@Preview
@Composable
private fun GameTypeSelectionScreenPreview() {
    QuizziTheme {
        GameTypeSelectionContent(
            gameTypes =
                listOf(
                    GameTypeUiModel(
                        GameType("Math"),
                        isSelected = false,
                    ),
                    GameTypeUiModel(
                        GameType("Science"),
                        isSelected = true,
                    ),
                    GameTypeUiModel(
                        GameType("History"),
                        isSelected = false,
                    ),
                    GameTypeUiModel(
                        GameType("Geography"),
                        isSelected = false,
                    ),
                    GameTypeUiModel(
                        GameType("Art"),
                        isSelected = false,
                    ),
                    GameTypeUiModel(
                        GameType("Music"),
                        isSelected = false,
                    ),
                    GameTypeUiModel(
                        GameType("Sports"),
                        isSelected = false,
                    ),
                    GameTypeUiModel(
                        GameType("Movies"),
                        isSelected = false,
                    ),
                ),
        )
    }
}
