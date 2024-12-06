package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.AuthErrorCode
import studio.astroturf.quizzi.domain.exceptionhandling.DialogAction
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionHandler
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.GameErrorCode
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.SnackbarAction
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.exceptionhandling.WebSocketErrorCode
import timber.log.Timber
import java.io.IOException

class DefaultExceptionHandler : ExceptionHandler {
    override fun handleException(exception: Exception): ExceptionResult {
        // Log all exceptions
        Timber.e(exception, "Error occurred: ${exception.message}")

        return when (exception) {
            is QuizziException -> handleQuizziException(exception)
            is IOException -> handleIOException(exception)
            else -> ExceptionResult.Fatal("An unexpected error occurred", exception)
        }
    }

    private fun handleQuizziException(exception: QuizziException): ExceptionResult =
        when (exception) {
            is QuizziException.AuthException -> handleAuthException(exception)
            is QuizziException.GameException -> handleGameException(exception)
            is QuizziException.HttpException -> handleHttpException(exception)
            is QuizziException.WebSocketException -> handleWebSocketException(exception)
            is QuizziException.UnexpectedException ->
                ExceptionResult.Fatal(
                    exception.message,
                    exception.cause ?: exception,
                )
        }

    private fun handleAuthException(exception: QuizziException.AuthException): ExceptionResult =
        when (exception.errorCode) {
            AuthErrorCode.SESSION_EXPIRED ->
                ExceptionResult.Notification(
                    UiNotification.Dialog(
                        title = "Session Expired",
                        message = "Your session has expired. Please log in again.",
                        primaryAction = DialogAction("Login") { /* Login action */ },
                        isDismissable = false,
                    ),
                )
            AuthErrorCode.INVALID_CREDENTIALS ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(
                        message = "Invalid username or password",
                        duration = UiNotification.Duration.LONG,
                    ),
                )
            AuthErrorCode.UNAUTHORIZED ->
                ExceptionResult.Notification(
                    UiNotification.Dialog(
                        message = "You are not authorized to perform this action",
                        primaryAction = DialogAction("OK") { },
                    ),
                )
            AuthErrorCode.USER_NOT_FOUND ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(message = "User not found"),
                )
            null -> ExceptionResult.Silent("Unspecified auth error: ${exception.message}")
        }

    private fun handleGameException(exception: QuizziException.GameException): ExceptionResult =
        when (exception.errorCode) {
            GameErrorCode.ROOM_NOT_FOUND ->
                ExceptionResult.Notification(
                    UiNotification.Dialog(
                        message = "Game room not found",
                        primaryAction = DialogAction("Return to Lobby") { },
                    ),
                )
            GameErrorCode.GAME_ALREADY_STARTED ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(message = "Game has already started"),
                )
            GameErrorCode.INVALID_GAME_STATE ->
                ExceptionResult.Silent(
                    "Invalid game state: ${exception.message}",
                )
            GameErrorCode.PLAYER_NOT_FOUND ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(message = "Player not found in game"),
                )
            GameErrorCode.ANSWER_ALREADY_SUBMITTED ->
                ExceptionResult.Notification(
                    UiNotification.Toast(message = "Answer already submitted"),
                )
            null -> ExceptionResult.Silent("Unspecified game error: ${exception.message}")
        }

    private fun handleHttpException(exception: QuizziException.HttpException): ExceptionResult {
        val message =
            when (exception.code) {
                401 -> "Authentication required"
                403 -> "Access denied"
                404 -> "Resource not found"
                500 -> "Server error occurred"
                else -> exception.message
            }

        return ExceptionResult.Notification(
            UiNotification.Snackbar(
                message = message,
                action = SnackbarAction("Retry") { /* Retry action */ },
            ),
        )
    }

    private fun handleWebSocketException(exception: QuizziException.WebSocketException): ExceptionResult =
        when (exception.code) {
            WebSocketErrorCode.CONNECTION_FAILED ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(
                        message = "Failed to connect to game server",
                        action = SnackbarAction("Retry") { /* Retry connection */ },
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
                        primaryAction = DialogAction("Retry") { /* Retry connection */ },
                        secondaryAction = DialogAction("Exit") { /* Exit game */ },
                    ),
                )
            null -> ExceptionResult.Silent("Unspecified WebSocket error: ${exception.message}")
        }

    private fun handleIOException(exception: IOException): ExceptionResult =
        ExceptionResult.Notification(
            UiNotification.Snackbar(
                message = "Network error. Please check your connection.",
                action = SnackbarAction("Retry") { /* Retry action */ },
                duration = UiNotification.Duration.LONG,
            ),
        )
}
