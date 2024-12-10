package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResolver
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.SnackbarAction
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolver that delegates exception handling to appropriate strategies.
 */
@Singleton
class DefaultExceptionResolver
    @Inject
    constructor(
        private val authExceptionStrategy: AuthExceptionStrategy,
        private val gameExceptionStrategy: GameExceptionStrategy,
        private val httpExceptionStrategy: HttpExceptionStrategy,
        private val webSocketExceptionStrategy: WebSocketExceptionStrategy,
        private val unexpectedExceptionStrategy: UnexpectedExceptionStrategy,
    ) : ExceptionResolver {
        override fun resolve(exception: Exception): ExceptionResult {
            // Log all exceptions
            Timber.e(exception, "Error occurred: ${exception.message}")

            return when (exception) {
                is QuizziException -> resolveQuizziException(exception)
                is IOException -> resolveIOException(exception)
                else -> ExceptionResult.Fatal("An unexpected error occurred", exception)
            }
        }

        private fun resolveQuizziException(exception: QuizziException): ExceptionResult =
            when (exception) {
                is QuizziException.AuthException -> authExceptionStrategy.resolve(exception)
                is QuizziException.GameException -> gameExceptionStrategy.resolve(exception)
                is QuizziException.HttpException -> httpExceptionStrategy.resolve(exception)
                is QuizziException.WebSocketException -> webSocketExceptionStrategy.resolve(exception)
                is QuizziException.UnexpectedException -> unexpectedExceptionStrategy.resolve(exception)
            }

        private fun resolveIOException(exception: IOException): ExceptionResult =
            ExceptionResult.Notification(
                UiNotification.Snackbar(
                    message = "Network error. Please check your connection.",
                    action = SnackbarAction("Retry") { /* Implement retry action */ },
                    duration = UiNotification.Duration.LONG,
                ),
            )
    }
