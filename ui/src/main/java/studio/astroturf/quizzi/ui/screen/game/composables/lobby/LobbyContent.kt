package studio.astroturf.quizzi.ui.screen.game.composables.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.domain.model.Player

@Composable
internal fun LobbyContent(
    roomName: String,
    creator: Player,
    challenger: Player?,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = roomName,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(32.dp))
        PlayerCard(player = creator, isCreator = true)
        Spacer(modifier = Modifier.height(16.dp))
        if (challenger != null) {
            PlayerCard(player = challenger, isCreator = false)
        } else {
            WaitingForOpponent()
        }
    }
}
