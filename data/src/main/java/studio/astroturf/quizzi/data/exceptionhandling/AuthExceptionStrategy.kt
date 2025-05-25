package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.AuthErrorCode
import studio.astroturf.quizzi.domain.exceptionhandling.DialogAction
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification

/**
 * Strategy for handling [QuizziException.AuthException].
 */
class AuthExceptionStrategy : ExceptionStrategy<QuizziException.AuthException> {
    override fun resolve(exception: QuizziException.AuthException): ExceptionResult =
        when (exception.errorCode) {
            AuthErrorCode.SESSION_EXPIRED ->
                ExceptionResult.Notification(
                    UiNotification.Dialog(
                        title = "Session Expired",
                        message = "Your session has expired. Please log in again.",
                        primaryAction = DialogAction("Login") { /* Implement login action */ },
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
                        primaryAction = DialogAction("OK") { /* Implement OK action */ },
                        isDismissable = true,
                    ),
                )
            AuthErrorCode.USER_NOT_FOUND ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(message = "User not found"),
                )
            AuthErrorCode.UNKNOWN ->
                ExceptionResult.Silent("Unspecified auth error: ${exception.message}")
        }
}
