import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()

    data class Success<T>(
        val data: T,
    ) : UiState<T>()

    data class Error(
        val message: String,
    ) : UiState<Nothing>()
}

fun <T> UiState<T>.isLoading() = this is UiState.Loading

fun <T> UiState<T>.isSuccess() = this is UiState.Success

fun <T> UiState<T>.isError() = this is UiState.Error

fun <T> UiState<T>.getOrNull(): T? =
    when (this) {
        is UiState.Success -> data
        else -> null
    }

@Composable
fun <T> Flow<UiState<T>>.collectAsStateWithLifecycle(initial: UiState<T> = UiState.Loading): State<UiState<T>> =
    produceState(initial) {
        collect { value = it }
    }

fun <T> StateFlow<UiState<T>>.onSuccess(action: (T) -> Unit) {
    if (value is UiState.Success) {
        action((value as UiState.Success<T>).data)
    }
}

fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> =
    when (this) {
        is UiState.Success -> UiState.Success(transform(data))
        is UiState.Error -> this
        is UiState.Loading -> this
        else -> throw IllegalStateException("Unknown state")
    }
