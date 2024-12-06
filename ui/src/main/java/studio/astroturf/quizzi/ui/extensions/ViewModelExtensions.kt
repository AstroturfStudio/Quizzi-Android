import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <T> ViewModel.safeLaunch(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    exceptionHandler: (Throwable) -> Unit = { },
    block: suspend () -> T,
) = viewModelScope.launch(dispatcher) {
    try {
        block()
    } catch (e: Exception) {
        exceptionHandler(e)
    }
}
