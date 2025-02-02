package studio.astroturf.quizzi.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.theme.Heading3
import studio.astroturf.quizzi.ui.theme.Primary
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.White

@Composable
fun AppBarScreen(
    modifier: Modifier = Modifier,
    title: String? = null,
    leadingIcon: ClickableIcon? = null,
    trailingIcon: ClickableIcon? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = Primary),
    ) {
        Box(
            modifier =
                Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            leadingIcon?.let {
                Icon(
                    modifier =
                        Modifier.size(24.dp).align(Alignment.CenterStart).clickable {
                            it.onClick?.invoke()
                        },
                    painter = painterResource(it.iconResId),
                    contentDescription = it.contentDescription,
                    tint = White,
                )
            }

            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    style = Heading3.copy(color = White),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(1f),
                )
            }

            trailingIcon?.let {
                Icon(
                    modifier =
                        Modifier.size(24.dp).align(Alignment.CenterEnd).clickable {
                            it.onClick?.invoke()
                        },
                    painter = painterResource(it.iconResId),
                    contentDescription = it.contentDescription,
                    tint = White,
                )
            }
        }

        content()
    }
}

data class ClickableIcon(
    @DrawableRes val iconResId: Int,
    val onClick: (() -> Unit)? = null,
    val contentDescription: String? = null,
)

@Preview
@Composable
private fun AppBarScreenPreview() {
    QuizziTheme {
        AppBarScreen(
            leadingIcon =
                ClickableIcon(
                    iconResId = R.drawable.ic_search,
                    onClick = {},
                ),
            title = "Alican",
            trailingIcon =
                ClickableIcon(
                    iconResId = R.drawable.ic_search,
                    onClick = {},
                ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = White),
            ) {
                Text(
                    text = "Content",
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}
