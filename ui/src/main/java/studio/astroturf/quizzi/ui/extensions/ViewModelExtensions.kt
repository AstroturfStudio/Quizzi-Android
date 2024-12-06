package studio.astroturf.quizzi.ui.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionHandler
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.result.QuizziResult
import timber.log.Timber

fun <T> ViewModel.handleQuizziResult(
    result: QuizziResult<T>,
    onSuccess: (T) -> Unit,
    exceptionHandler: ExceptionHandler,
    onUiNotification: (UiNotification) -> Unit,
    onFatalException: (String, Throwable) -> Unit = { _, _ -> },
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    when (result) {
        is QuizziResult.Success -> onSuccess(result.value)
        is QuizziResult.Failure -> {
            viewModelScope.launch(dispatcher) {
                when (val handleResult = exceptionHandler.handleException(result.exception)) {
                    is ExceptionResult.Notification -> onUiNotification(handleResult.notification)
                    is ExceptionResult.Silent -> Timber.e(handleResult.logMessage)
                    is ExceptionResult.Fatal -> {
                        Timber.e(handleResult.throwable, handleResult.message)
                        onFatalException(handleResult.message, handleResult.throwable)
                    }
                }
            }
        }
    }
}
