package studio.astroturf.quizzi.domain.network

sealed class GameConnectionStatus {
    data object Idle : GameConnectionStatus()

    data object Connected : GameConnectionStatus()

    data object Disconnected : GameConnectionStatus()

    data class Reconnecting(
        val attempt: Int,
    ) : GameConnectionStatus()

    data object Failed : GameConnectionStatus()
}
