package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.ui.screen.game.GameUiState.RoundOn.PlayerRoundResult

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
            isSelected -> Color(0xFFFDD835) // Sarı
            // Seçilmemiş normal durum
            else -> Color.White
        }

    val textColor =
        when {
            playerRoundResult != null && isSelected -> Color.White
            isSelected -> Color.Black
            else -> Color.Black
        }

    Button(
        onClick = onClick,
        enabled = selectedAnswerId == null, // Bir cevap seçildiyse disable et
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                disabledContainerColor = containerColor, // Disable durumunda da aynı rengi koru
            ),
        modifier =
            modifier
                .height(56.dp)
                .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}
