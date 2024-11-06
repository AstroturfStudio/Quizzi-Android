package com.alicankorkmaz.flagquiz.data.remote

import com.alicankorkmaz.flagquiz.data.BuildConfig
import com.alicankorkmaz.flagquiz.domain.model.websocket.GameMessage
import com.alicankorkmaz.flagquiz.domain.model.websocket.GameMessage.ConnectionStateType
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
import java.util.UUID
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
    private val messageChannel = Channel<GameMessage>()
    private var playerId: String? = null
    private var isReconnecting = false
    private val reconnectJob = Job()
    private val reconnectScope = CoroutineScope(Dispatchers.IO + reconnectJob)
    private var reconnectAttempt = 0

    fun connect(existingPlayerId: String? = null) {
        playerId = existingPlayerId ?: UUID.randomUUID().toString()
        connectWebSocket()
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
                val delayMs = min(
                    INITIAL_BACKOFF_MS * (2.0.pow(reconnectAttempt - 1)).toLong(),
                    MAX_BACKOFF_MS
                )
                delay(delayMs)

                playerId?.let { existingId ->
                    connectWebSocket()
                    val reconnectMessage = GameMessage.ConnectionState(
                        connectionStateType = ConnectionStateType.RECONNECT_REQUEST,
                        playerId = existingId
                    )
                    send(reconnectMessage)
                }
            } catch (e: Exception) {
                handleReconnectFailure()
            }
        }
    }

    private fun handleReconnectFailure() {
        if (reconnectAttempt < MAX_RECONNECT_ATTEMPTS) {
            handleReconnect()
        } else {
            messageChannel.trySend(
                GameMessage.ConnectionState(
                    connectionStateType = ConnectionStateType.RECONNECT_FAILED,
                    playerId = playerId ?: "",
                    reason = "Maximum reconnection attempts reached"
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

    fun observeMessages(): Flow<GameMessage> = messageChannel.receiveAsFlow()

    fun send(message: GameMessage) {
        val jsonString = json.encodeToString(GameMessage.serializer(), message)
        webSocket?.send(jsonString)
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Timber.d("WebSocket Connection Established")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val message = json.decodeFromString<GameMessage>(text)
                messageChannel.trySend(message)
            } catch (e: Exception) {
                Timber.e(e, "Error parsing WebSocket message")
                messageChannel.trySend(
                    GameMessage.Error(
                        message = "Error parsing message: ${e.message}"
                    )
                )
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