package com.alicankorkmaz.quizzi.data.remote

import com.alicankorkmaz.quizzi.data.BuildConfig
import com.alicankorkmaz.quizzi.domain.model.websocket.ClientSocketMessage
import com.alicankorkmaz.quizzi.domain.model.websocket.ServerSocketMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.pow

class WebSocketService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
        isLenient = true
    }
    private var webSocket: WebSocket? = null
    private val messageChannel = Channel<ServerSocketMessage>()
    private var playerId: String? = null
    private var isReconnecting = false
    private val reconnectJob = Job()
    private val reconnectScope = CoroutineScope(Dispatchers.IO + reconnectJob)
    private var reconnectAttempt = 0

    private fun createReconnectMessage(playerId: String) =
        ClientSocketMessage.PlayerReconnected(playerId = playerId)

    fun connect(playerId: String? = null) {
        this.playerId = playerId
        connectWebSocket()
        playerId?.let { send(createReconnectMessage(it)) }
    }

    private fun connectWebSocket() {
        val request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/game")
            .build()
        webSocket = client.newWebSocket(request, createWebSocketListener())
    }

    private fun handleReconnect() {
        if (isReconnecting) return
        isReconnecting = true
        reconnectAttempt++

        reconnectScope.launch {
            try {
                val delayMs = calculateBackoffDelay()
                delay(delayMs)
                playerId?.let { existingId ->
                    connectWebSocket()
                    send(createReconnectMessage(existingId))
                }
            } catch (e: Exception) {
                handleReconnectFailure()
            }
        }
    }

    private fun calculateBackoffDelay() = min(
        INITIAL_BACKOFF_MS * (2.0.pow(reconnectAttempt - 1)).toLong(),
        MAX_BACKOFF_MS
    )

    private fun handleReconnectFailure() {
        if (reconnectAttempt < MAX_RECONNECT_ATTEMPTS) {
            handleReconnect()
        } else {
            messageChannel.trySend(
                ServerSocketMessage.PlayerDisconnected(
                    playerId = playerId ?: "",
                    playerName = ""
                )
            )
            resetReconnectState()
        }
    }

    private fun resetReconnectState() {
        isReconnecting = false
        reconnectAttempt = 0
        reconnectJob.cancel()
    }

    fun disconnect() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, "User disconnected")
        webSocket = null
    }

    fun observeMessages(): Flow<ServerSocketMessage> = messageChannel.receiveAsFlow()

    fun send(message: ClientSocketMessage) {
        val jsonString = json.encodeToString(ClientSocketMessage.serializer(), message)
        webSocket?.send(jsonString)
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Timber.d("WebSocket Connection Established")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val message = json.decodeFromString<ServerSocketMessage>(text)
                when (message) {
                    is ServerSocketMessage.JoinedRoom -> handleJoinedRoom(message)
                    else -> messageChannel.trySend(message)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error parsing WebSocket message")
            }
        }

        private fun handleJoinedRoom(message: ServerSocketMessage.JoinedRoom) {
            if (message.success) {
                messageChannel.trySend(message)
            } else {
                messageChannel.trySend(ServerSocketMessage.Error("Failed to join room"))
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Timber.d("WebSocket Closing: $code - $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Timber.d("WebSocket Closed: $code - $reason")
            if (code != NORMAL_CLOSURE_STATUS) {
                handleReconnect()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.e(t, "WebSocket Error")
            handleReconnect()
        }
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val MAX_RECONNECT_ATTEMPTS = 5
        private const val INITIAL_BACKOFF_MS = 1000L
        private const val MAX_BACKOFF_MS = 32000L
    }
} 