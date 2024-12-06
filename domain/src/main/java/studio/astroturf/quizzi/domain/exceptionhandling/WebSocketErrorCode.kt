package studio.astroturf.quizzi.domain.exceptionhandling

enum class WebSocketErrorCode {
    CONNECTION_FAILED,
    CONNECTION_LOST,
    INVALID_MESSAGE,
    RECONNECTION_FAILED,
}
