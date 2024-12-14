package studio.astroturf.quizzi.data.network

sealed class NetworkStatus {
    data class Connected(
        val type: ConnectionType,
    ) : NetworkStatus()

    object NotConnected : NetworkStatus()
}
