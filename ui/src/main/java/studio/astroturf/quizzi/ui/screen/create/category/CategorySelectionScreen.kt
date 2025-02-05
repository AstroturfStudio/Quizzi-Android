package studio.astroturf.quizzi.ui.screen.create.category

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.AppBarScreen
import studio.astroturf.quizzi.ui.components.ClickableIcon
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.BodyXSmallRegular
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.Secondary
import studio.astroturf.quizzi.ui.theme.White

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun CategorySelectionScreen(
    onProceed: (Category?) -> Unit,
    onBackPress: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val categories by viewModel.categoriesUiModel.collectAsState()

    AppBarScreen(
        title = "Choose Category",
        leadingIcon =
            ClickableIcon(
                iconResId = R.drawable.ic_back,
                onClick = { onBackPress() },
            ),
    ) {
        CategorySelectionContent(
            categories = categories,
            onCategoryClick = { viewModel.selectCategory(it) },
            onNextClick = { onProceed(viewModel.getSelectedCategory()) },
        )
    }
}

@Composable
private fun CategorySelectionContent(
    categories: List<CategoryUiModel>,
    onCategoryClick: (CategoryUiModel) -> Unit = {},
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
            categories.forEach {
                item {
                    Column(
                        modifier =
                            Modifier
                                .height(132.dp)
                                .fillMaxWidth()
                                .background(
                                    color = if (it.isSelected) Secondary else Grey5,
                                    shape = RoundedCornerShape(size = 20.dp),
                                ).clickable { onCategoryClick(it) }
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
                            Icon(
                                painter = painterResource(id = R.drawable.ic_math),
                                tint = if (it.isSelected) White else Secondary,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }

                        Text(
                            modifier = Modifier.wrapContentSize(),
                            text = it.category.name,
                            style = BodyNormalMedium.copy(color = if (it.isSelected) White else Secondary),
                            maxLines = 1,
                        )

                        Text(
                            modifier = Modifier.wrapContentSize(),
                            text = "2 Quizzes",
                            style =
                                BodyXSmallRegular.copy(
                                    color =
                                        if (it.isSelected) {
                                            White.copy(
                                                alpha = 0.8f,
                                            )
                                        } else {
                                            Secondary
                                        },
                                ),
                        )
                    }
                }
            }
        }

        Button(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = Secondary, shape = RoundedCornerShape(size = 20.dp))
                    .align(Alignment.BottomCenter),
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors().copy(containerColor = Secondary),
            enabled = categories.any { it.isSelected },
        ) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "Next",
                style = BodyNormalMedium.copy(color = White),
            )
        }
    }
}

@Preview
@Composable
private fun CategorySelectionScreenPreview() {
    QuizziTheme {
        CategorySelectionContent(
            categories =
                listOf(
                    CategoryUiModel(
                        category = Category(0, "Math"),
                        isSelected = false,
                    ),
                    CategoryUiModel(
                        category = Category(1, "Science"),
                        isSelected = true,
                    ),
                    CategoryUiModel(
                        category = Category(2, "History"),
                        isSelected = false,
                    ),
                    CategoryUiModel(
                        category = Category(3, "Geography"),
                        isSelected = false,
                    ),
                ),
        )
    }
}
