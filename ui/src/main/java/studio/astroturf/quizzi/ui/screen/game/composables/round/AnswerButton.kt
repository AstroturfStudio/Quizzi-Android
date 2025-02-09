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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult
import studio.astroturf.quizzi.ui.theme.Accent1
import studio.astroturf.quizzi.ui.theme.Black
import studio.astroturf.quizzi.ui.theme.BodyNormalMedium
import studio.astroturf.quizzi.ui.theme.BodyNormalRegular
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
                .height(56.dp)
                .border(width = 2.dp, color = Grey5, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp),
            textAlign = TextAlign.Start,
            text = text,
            style = if (isSelected) BodyNormalMedium else BodyNormalRegular,
            color = Black,
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
