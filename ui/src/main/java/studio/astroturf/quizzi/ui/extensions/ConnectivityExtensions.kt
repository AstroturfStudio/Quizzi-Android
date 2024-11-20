import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
    data class Connected(val type: ConnectionType) : NetworkStatus()
    data class Error(val message: String) : NetworkStatus()
}

enum class ConnectionType {
    WIFI,
    CELLULAR,
    ETHERNET,
    VPN,
    UNKNOWN
}

@SuppressLint("MissingPermission")
fun Context.observeNetworkStatus(): Flow<NetworkStatus> = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            val connectionType = when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ->
                    ConnectionType.WIFI

                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ->
                    ConnectionType.CELLULAR

                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true ->
                    ConnectionType.ETHERNET

                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true ->
                    ConnectionType.VPN

                else -> ConnectionType.UNKNOWN
            }
            trySend(NetworkStatus.Connected(connectionType))
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            trySend(NetworkStatus.Unavailable)
        }

        override fun onLost(network: Network) {
            trySend(NetworkStatus.Unavailable)
        }

        override fun onUnavailable() {
            trySend(NetworkStatus.Unavailable)
        }
    }

    val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(request, networkCallback)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}.distinctUntilChanged()

@SuppressLint("MissingPermission")
fun Context.isOnMeteredConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.isActiveNetworkMetered
}