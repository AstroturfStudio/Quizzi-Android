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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.domain.model.CategoryId
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
fun CategorySelectionScreen(
    onProceed: (Category?) -> Unit,
    onBackPress: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val categories by viewModel.categoriesUiModel.collectAsState()

    AppBarScreen(
        title = stringResource(R.string.choose_category),
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
                                painter =
                                    painterResource(
                                        id =
                                            when (it.category.id) {
                                                CategoryId.COUNTRY_FLAGS -> R.drawable.ic_flag
                                                CategoryId.COUNTRY_CAPITALS -> R.drawable.ic_capital
                                                CategoryId.HOLLYWOOD_STARS -> R.drawable.ic_star
                                                CategoryId.MOVIE_POSTERS -> R.drawable.ic_movie
                                                CategoryId.FOOTBALL_CLUB_LOGOS -> R.drawable.ic_football
                                            },
                                    ),
                                tint = if (it.isSelected) White else Secondary,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }

                        Text(
                            modifier =
                                Modifier
                                    .wrapContentSize()
                                    .fillMaxWidth(),
                            text = stringResource(it.categoryNameResId),
                            style = BodyNormalMedium.copy(color = if (it.isSelected) White else Secondary),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        QButton.Secondary(
            text = stringResource(R.string.next),
            modifier = Modifier.align(Alignment.BottomCenter),
            enabled = categories.any { it.isSelected },
            onClick = onNextClick,
        )
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
                        category = Category(CategoryId.COUNTRY_FLAGS, "Math"),
                        categoryNameResId = R.string.category_country_flags,
                        isSelected = false,
                    ),
                    CategoryUiModel(
                        category = Category(CategoryId.COUNTRY_CAPITALS, "Science"),
                        categoryNameResId = R.string.category_country_capitals,
                        isSelected = true,
                    ),
                    CategoryUiModel(
                        category = Category(CategoryId.HOLLYWOOD_STARS, "History"),
                        categoryNameResId = R.string.category_hollywood_stars,
                        isSelected = false,
                    ),
                    CategoryUiModel(
                        category = Category(CategoryId.MOVIE_POSTERS, "Geography"),
                        categoryNameResId = R.string.category_movie_posters,
                        isSelected = false,
                    ),
                ),
        )
    }
}
