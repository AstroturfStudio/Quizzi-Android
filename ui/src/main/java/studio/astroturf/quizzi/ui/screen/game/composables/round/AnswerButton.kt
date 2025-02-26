package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import studio.astroturf.quizzi.ui.theme.Accent1
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.BodyNormalRegular
import studio.astroturf.quizzi.ui.theme.BodySmallMedium
import studio.astroturf.quizzi.ui.theme.BodySmallRegular
import studio.astroturf.quizzi.ui.theme.Grey5
import studio.astroturf.quizzi.ui.theme.White

@Composable
internal fun AnswerButton(
    text: String,
    onClick: () -> Unit,
    optionId: Int,
    selectedAnswerId: Int?,
    playerRoundResult: PlayerRoundResult?,
    modifier: Modifier = Modifier,
) {
    val isSelected = selectedAnswerId == optionId
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    
    // Adjust padding based on screen dimensions
    val horizontalPadding = when {
        screenWidth < 360 || screenHeight < 600 -> 8.dp
        screenWidth < 400 -> 12.dp
        else -> 24.dp
    }
    
    // Adjust corner radius based on screen dimensions
    val cornerRadius = when {
        screenWidth < 360 || screenHeight < 600 -> 12.dp
        screenWidth < 400 -> 16.dp
        else -> 20.dp
    }
    
    // Adjust border width based on screen dimensions
    val borderWidth = if (screenWidth < 360 || screenHeight < 600) 1.dp else 2.dp

    val containerColor =
        when {
            // Cevap sonucu geldiğinde ve bu buton seçilmişse
            playerRoundResult != null && isSelected -> {
                if (playerRoundResult.isCorrect) {
                    Color(0xFF4CAF50) // Yeşil
                } else {
                    Color(0xFFF44336) // Kırmızı
                }
            }
            // Sadece seçilmişse ve sonuç henüz gelmediyse
            isSelected -> Accent1
            // Seçilmemiş normal durum
            else -> White
        }

    val alpha = if (playerRoundResult == null && isSelected) 0.5f else 1f
    
    // Choose text style based on screen size
    val textStyle = if (screenHeight < 600) {
        if (isSelected) BodySmallMedium else BodySmallRegular
    } else {
        if (isSelected) BodyNormalMedium else BodyNormalRegular
    }

    Button(
        onClick = onClick,
        enabled = selectedAnswerId == null, // Bir cevap seçildiyse disable et
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor.copy(alpha = alpha),
                disabledContainerColor = containerColor.copy(alpha = alpha), // Disable durumunda da aynı rengi koru
            ),
        modifier =
            modifier
                .border(width = borderWidth, color = Grey5, shape = RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horizontalPadding, end = 4.dp),
            textAlign = TextAlign.Start,
            text = text,
            style = textStyle,
            color = Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun AnswerButtonPreview() {
    AnswerButton(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        text = "Option 1",
        onClick = {},
        optionId = 1,
        selectedAnswerId = 1,
        playerRoundResult = null,
    )
}

@Preview
@Composable
private fun AnswerButtonLongTextPreview() {
    AnswerButton(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp),
        text = "This is a very long option text that might need to be displayed on multiple lines",
        onClick = {},
        optionId = 1,
        selectedAnswerId = null,
        playerRoundResult = null,
    )
}
