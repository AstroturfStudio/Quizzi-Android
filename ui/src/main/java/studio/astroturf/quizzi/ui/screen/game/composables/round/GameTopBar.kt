package studio.astroturf.quizzi.ui.screen.game.composables.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameTopBar(
    timeRemaining: Int,
    cursorPosition: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TimeDisplay(
            timeRemaining = timeRemaining,
            modifier = Modifier.size(56.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        GameBar(
            cursorPosition = cursorPosition,
            modifier =
                Modifier
                    .weight(1f)
                    .height(8.dp), // Fixed height for GameBar
        )
    }
}
