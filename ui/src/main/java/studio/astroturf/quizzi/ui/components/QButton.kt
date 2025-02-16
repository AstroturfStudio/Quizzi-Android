package studio.astroturf.quizzi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.QuizziTheme
import studio.astroturf.quizzi.ui.theme.Secondary
import studio.astroturf.quizzi.ui.theme.White

@Composable
fun QButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit = { },
) {
    Button(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .then(modifier),
        onClick = onClick,
        shape = RoundedCornerShape(size = 20.dp),
        enabled = enabled,
        colors = colors,
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = text,
            textAlign = TextAlign.Center,
            style = BodyNormalMedium,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun QButtonPreview() {
    QuizziTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QButton.Primary(
                text = "Primary Enabled",
            )

            QButton.Primary(
                text = "Primary Disabled",
                enabled = false,
            )

            QButton.Secondary(
                text = "Secondary Enabled",
            )

            QButton.Secondary(
                text = "Secondary Disabled",
                enabled = false,
            )
        }
    }
}

object QButton {
    @Composable
    fun Primary(
        text: String,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        onClick: () -> Unit = { },
    ) {
        QButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            onClick = onClick,
        )
    }

    @Composable
    fun Secondary(
        text: String,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        onClick: () -> Unit = { },
    ) {
        QButton(
            text = text,
            modifier = modifier,
            enabled = enabled,
            colors =
                ButtonDefaults.buttonColors().copy(
                    containerColor = Secondary,
                    contentColor = White,
                ),
            onClick = onClick,
        )
    }
}
