package studio.astroturf.quizzi.ui.screen.game

import studio.astroturf.quizzi.domain.model.statemachine.GameState

fun GameState.toUiState(): GameUiState {
    return when (this) {
        GameState.Idle -> {
            GameUiState.Idle
        }

        is GameState.Lobby -> {
            val creator = players.first()
            GameUiState.Lobby(
                roomName = "${creator.name} 's Room",
                creator = creator,
                challenger = players.getOrNull(1)
            )
        }

        is GameState.EndOfRound -> {
            GameUiState.RoundEnd(
                roundNo = 0, // TODO: Gelsinnn
                roundWinner = winnerPlayer,
                correctAnswerValue = correctAnswer.value,
                newCursorPosition = cursorPosition
            )
        }

        is GameState.GameOver -> {
            GameUiState.GameOver(
                totalRoundCount = statistics.roundCount,
                winner = winner
            )
        }

        is GameState.Paused -> {
            GameUiState.Paused(
                reason = reason,
                onlinePlayers = onlinePlayers
            )
        }

        is GameState.RoundOn -> {
            GameUiState.RoundOn(
                player1 = players[0],
                player2 = players[1],
                gameBarPercentage = cursorPosition,
                question = currentQuestion,
                timeRemainingInSeconds = timeRemaining.toInt(),
            )
        }

        is GameState.Starting -> {
            GameUiState.Starting(
                timeRemainingInSeconds = timeRemaining
            )
        }
    }
}