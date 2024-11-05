package com.alicankorkmaz.flagquiz.data.remote

import com.alicankorkmaz.flagquiz.data.BuildConfig
import com.alicankorkmaz.flagquiz.domain.model.websocket.GameMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import timber.log.Timber
import java.util.concurrent.TimeUnit

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

    fun connect() {
        val request = Request.Builder()
            .url(BuildConfig.BASE_URL)
            .build()

        webSocket = client.newWebSocket(request, createWebSocketListener())
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
                    GameMessage.ErrorMessage(
                        reason = "Error parsing message: ${e.message}"
                    )
                )
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Timber.d("WebSocket Closing: $code - $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Timber.d("WebSocket Closed: $code - $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.e(t, "WebSocket Error")
            messageChannel.trySend(
                GameMessage.ErrorMessage(
                    reason = "Connection error: ${t.message}"
                )
            )
        }
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
} 