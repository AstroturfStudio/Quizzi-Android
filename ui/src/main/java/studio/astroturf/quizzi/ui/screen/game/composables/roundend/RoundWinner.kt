package studio.astroturf.quizzi.ui.screen.game.composables.roundend

sealed class RoundWinner(
    val winnerId: String?,
    val winnerName: String?,
) {
    object None : RoundWinner(null, null)

    data class Me(
        val id: String,
        val name: String,
    ) : RoundWinner(id, name)

    data class Opponent(
        val id: String,
        val name: String,
    ) : RoundWinner(id, name)
}
