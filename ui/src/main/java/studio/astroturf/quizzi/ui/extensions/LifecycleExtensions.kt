import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun LifecycleOwner.launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job =
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }

fun LifecycleOwner.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job =
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            block()
        }
    }

fun <T> Flow<T>.collectWhenStarted(
    lifecycleOwner: LifecycleOwner,
    collector: suspend (T) -> Unit,
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { collector(it) }
        }
    }
}

fun <T> Flow<T>.collectWhenResumed(
    lifecycleOwner: LifecycleOwner,
    collector: suspend (T) -> Unit,
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            collect { collector(it) }
        }
    }
}
