package studio.astroturf.quizzi.data.remote.websocket.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import studio.astroturf.quizzi.data.BuildConfig
import studio.astroturf.quizzi.data.di.WebSocketClient
import studio.astroturf.quizzi.data.remote.websocket.model.ClientSocketMessage
import studio.astroturf.quizzi.data.remote.websocket.model.ServerSocketMessage
import studio.astroturf.quizzi.domain.di.IoDispatcher
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.WebSocketErrorCode
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class QuizziWebSocketService
    @Inject
    constructor(
        @WebSocketClient private val client: OkHttpClient,
        private val json: Json,
        @IoDispatcher private val serviceDispatcher: CoroutineDispatcher,
    ) {
        private var webSocket: WebSocket? = null
        private var playerId: String? = null

        private val serviceScope = CoroutineScope(serviceDispatcher + SupervisorJob())

        private val _messageFlow =
            MutableSharedFlow<ServerSocketMessage>(
                replay = 0,
                extraBufferCapacity = Channel.UNLIMITED,
                onBufferOverflow = BufferOverflow.SUSPEND,
            )
        val messageFlow: Flow<ServerSocketMessage> = _messageFlow.asSharedFlow()

        private var reconnectAttempts = 0
        private val maxReconnectAttempts = 5
        private val initialDelayMillis = 1000L // 1 second

        fun connect(playerId: String? = null) {
            this.playerId = playerId
            val request =
                Request
                    .Builder()
                    .url("${BuildConfig.BASE_URL.replace("http", "ws")}/game?playerId=$playerId")
                    .build()
            webSocket = client.newWebSocket(request, createWebSocketListener())
        }

        fun disconnect() {
            webSocket?.close(NORMAL_CLOSURE_STATUS, "User disconnected")
            webSocket = null
            serviceScope.cancel()
        }

        fun observeMessages(): Flow<ServerSocketMessage> = messageFlow

        fun send(message: ClientSocketMessage) {
            try {
                val jsonString = json.encodeToString(ClientSocketMessage.serializer(), message)
                webSocket?.send(jsonString)
            } catch (e: Exception) {
                Timber.e(e, "Error sending message")
                serviceScope.launch {
                    _messageFlow.emit(ServerSocketMessage.Error("Failed to send message: ${e.message}"))
                }
            }
        }

        private fun createWebSocketListener() =
            object : WebSocketListener() {
                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response,
                ) {
                    Timber.d("WebSocket Connection Established")
                    reconnectAttempts = 0
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String,
                ) {
                    try {
                        Timber.d("Incoming WebSocket Message:\n$text")
                        val message = json.decodeFromString<ServerSocketMessage>(text)
                        serviceScope.launch {
                            _messageFlow.emit(message)
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing WebSocket message: $text")
                        serviceScope.launch {
                            _messageFlow.emit(ServerSocketMessage.Error("Failed to parse message: ${e.message}"))
                        }
                    }
                }

                override fun onClosing(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String,
                ) {
                    Timber.d("WebSocket Closing: $code - $reason")
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String,
                ) {
                    Timber.d("WebSocket Closed: $code - $reason")
                    attemptReconnect()
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?,
                ) {
                    Timber.e(t, "WebSocket Error")
                    handleFailure(t)
                    attemptReconnect()
                }
            }

        private fun handleFailure(t: Throwable) {
            val exception =
                QuizziException.WebSocketException(
                    message = t.message ?: "WebSocket connection failed",
                    code = WebSocketErrorCode.CONNECTION_FAILED,
                    cause = t,
                )
            // Handle the Exception
        }

        private fun attemptReconnect() {
            if (reconnectAttempts >= maxReconnectAttempts) {
                Timber.e("Max reconnection attempts reached")
                // Optionally, notify the user about the failed reconnection
                return
            }
            reconnectAttempts++
            val delayMillis = initialDelayMillis * 2.0.pow(reconnectAttempts.toDouble()).toLong()
            Timber.d("Attempting to reconnect in $delayMillis ms")
            serviceScope.launch {
                delay(delayMillis)
                connect(playerId)
            }
        }

        companion object {
            private const val NORMAL_CLOSURE_STATUS = 1000
        }
    }
