package studio.astroturf.quizzi.ui.screen.onboarding

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.QButton
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.BodyNormalRegular
import studio.astroturf.quizzi.ui.theme.Grey2
import studio.astroturf.quizzi.ui.theme.Heading3
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.White
import timber.log.Timber

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingConstants.onboardingPages.size })

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            Timber.tag("Page change").d("Page changed to $page")
        }
    }

    OnboardingContent(
        pagerState = pagerState,
        onSignUpClick = {
            viewModel.completeOnboarding()
            onSignUpClick()
        },
        onLoginClick = {
            viewModel.completeOnboarding()
            onLoginClick()
        },
    )
}

@Composable
fun OnboardingContent(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = Primary),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) { page ->

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Image(
                    modifier = Modifier.align(Alignment.Center),
                    contentDescription = null,
                    painter = painterResource(id = OnboardingConstants.onboardingPages[page].imageRes),
                )
            }
        }

        Row(
            Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pagerState.pageCount) { iteration ->
                if (iteration == pagerState.currentPage) {
                    // Selected indicator with ring
                    Box(
                        modifier =
                            Modifier
                                .size(16.dp)
                                .border(
                                    width = 2.dp,
                                    color = White,
                                    shape = CircleShape,
                                ),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .align(Alignment.Center)
                                    .background(
                                        color = White,
                                        shape = CircleShape,
                                    ),
                        )
                    }
                } else {
                    // Unselected indicator
                    Box(
                        modifier =
                            Modifier
                                .size(8.dp)
                                .background(
                                    color = White,
                                    shape = CircleShape,
                                ),
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier =
                Modifier
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(OnboardingConstants.onboardingPages[pagerState.currentPage].titleStringRes),
                style = Heading3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(24.dp))

            QButton(
                text = stringResource(R.string.sign_up),
                onClick = { onSignUpClick() },
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.already_have_an_account),
                    style = BodyNormalRegular.copy(color = Grey2),
                )
                Text(
                    text = stringResource(R.string.login),
                    style = BodyNormalMedium.copy(color = Primary),
                    modifier = Modifier.clickable { onLoginClick() },
                )
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingPreview() {
    QuizziTheme {
        OnboardingContent(
            pagerState = rememberPagerState(pageCount = { 3 }),
        )
    }
}
