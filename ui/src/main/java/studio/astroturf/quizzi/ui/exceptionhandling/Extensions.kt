package studio.astroturf.quizzi.ui.exceptionhandling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionHandler
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import timber.log.Timber

fun ViewModel.handleException(
    exception: Exception,
    exceptionHandler: ExceptionHandler,
    onNotification: (UiNotification) -> Unit,
    onFatalError: (String, Throwable) -> Unit = { _, _ -> },
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    viewModelScope.launch(dispatcher) {
        when (val result = exceptionHandler.handleError(exception)) {
            is ExceptionResult.Notification -> onNotification(result.notification)
            is ExceptionResult.Silent -> Timber.e(exception, result.logMessage)
            is ExceptionResult.Fatal -> {
                Timber.e(exception, result.message)
                onFatalError(result.message, result.throwable)
            }
        }
    }
}
