package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.SnackbarAction
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification

/**
 * Strategy for handling [QuizziException.HttpException].
 */
class HttpExceptionStrategy : ExceptionStrategy<QuizziException.HttpException> {
    override fun resolve(exception: QuizziException.HttpException): ExceptionResult {
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
                action = SnackbarAction("Retry") { /* Implement retry action */ },
            ),
        )
    }
}
