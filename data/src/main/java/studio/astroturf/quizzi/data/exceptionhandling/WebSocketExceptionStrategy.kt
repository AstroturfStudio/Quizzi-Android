package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.DialogAction
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.SnackbarAction
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.exceptionhandling.WebSocketErrorCode

/**
 * Strategy for handling [QuizziException.WebSocketException].
 */
class WebSocketExceptionStrategy : ExceptionStrategy<QuizziException.WebSocketException> {
    override fun resolve(exception: QuizziException.WebSocketException): ExceptionResult =
        when (exception.code) {
            WebSocketErrorCode.CONNECTION_FAILED ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(
                        message = "Failed to connect to game server",
                        action = SnackbarAction("Retry") { /* Implement retry connection */ },
                    ),
                )
            WebSocketErrorCode.CONNECTION_LOST ->
                ExceptionResult.Notification(
                    UiNotification.Dialog(
                        message = "Connection lost. Attempting to reconnect...",
                        isDismissable = false,
                    ),
                )
            WebSocketErrorCode.INVALID_MESSAGE ->
                ExceptionResult.Silent(
                    "Invalid WebSocket message: ${exception.message}",
                )
            WebSocketErrorCode.RECONNECTION_FAILED ->
                ExceptionResult.Notification(
                    UiNotification.Dialog(
                        title = "Connection Error",
                        message = "Failed to reconnect to game server",
                        primaryAction = DialogAction("Retry") { /* Implement retry connection */ },
                        secondaryAction = DialogAction("Exit") { /* Implement exit game */ },
                        isDismissable = false,
                    ),
                )
            WebSocketErrorCode.UNKNOWN ->
                ExceptionResult.Silent("Unspecified WebSocket error: ${exception.message}")
        }
}
