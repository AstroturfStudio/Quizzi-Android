package studio.astroturf.quizzi.ui.extensions

import androidx.lifecycle.ViewModel
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResolver
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.result.QuizziResult
import studio.astroturf.quizzi.domain.result.fold
import timber.log.Timber

context(ViewModel)
fun <T, R> QuizziResult<T>.resolve(
    resolver: ExceptionResolver,
    onUiNotification: ((UiNotification) -> Unit)? = null,
    onFatalException: ((String, Throwable) -> Unit)? = null,
    onSuccess: (value: T) -> R,
) {
    fold(
        onSuccess = onSuccess,
        onFailure = { exception: QuizziException ->
            when (val exceptionResult = resolver.resolve(exception)) {
                is ExceptionResult.Notification -> {
                    onUiNotification?.invoke(exceptionResult.notification)
                }
                is ExceptionResult.Silent -> {
                    Timber.e(exceptionResult.logMessage)
                }
                is ExceptionResult.Fatal -> {
                    Timber.e(exceptionResult.throwable, exceptionResult.message)
                    onFatalException?.invoke(exceptionResult.message, exceptionResult.throwable)
                }
            }
        },
    )
}
