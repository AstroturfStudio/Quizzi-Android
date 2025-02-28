package studio.astroturf.quizzi.ui.screen.game.composables.gameover

import BugReportDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import studio.astroturf.quizzi.domain.model.GameFeedback
import studio.astroturf.quizzi.ui.R
import studio.astroturf.quizzi.ui.components.QButton
import studio.astroturf.quizzi.ui.screen.game.GameUiState

@Composable
internal fun GameOverContent(
    state: GameUiState.GameOver,
    onNavigateBack: () -> Unit,
    onSubmitFeedback: (GameFeedback) -> Unit,
) {
    var showBugReportDialog by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.game_over),
            style = MaterialTheme.typography.displayMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.winner, state.winnerName),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.total_rounds, state.totalRoundCount),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(32.dp))

        FeedbackSection(
            onSubmitFeedback = onSubmitFeedback,
            onReportBug = { showBugReportDialog = true },
        )

        Spacer(modifier = Modifier.height(32.dp))

        QButton(
            text = stringResource(R.string.back_to_rooms),
            onClick = onNavigateBack,
        )
    }

    if (showBugReportDialog) {
        BugReportDialog(
            gameId = state.gameId,
            onDismiss = { showBugReportDialog = false },
        )
    }
}
