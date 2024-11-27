import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun <T> Flow<T>.throttleFirst(windowDuration: Duration = 1.seconds): Flow<T> =
    flow {
        var lastEmissionTime = 0L
        collect { upstream ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEmissionTime >= windowDuration.inWholeMilliseconds) {
                lastEmissionTime = currentTime
                emit(upstream)
            }
        }
    }

fun <T> Flow<T>.throttleLast(windowDuration: Duration = 1.seconds): Flow<T> =
    flow {
        var lastValue: T? = null
        var lastEmissionTime = 0L

        collect { upstream ->
            val currentTime = System.currentTimeMillis()
            lastValue = upstream

            if (currentTime - lastEmissionTime >= windowDuration.inWholeMilliseconds) {
                lastValue?.let {
                    emit(it)
                    lastEmissionTime = currentTime
                    lastValue = null
                }
            }
        }
    }

fun <T> Flow<T>.debounce(waitMillis: Long): Flow<T> =
    flow {
        var lastValue: T? = null
        var lastEmissionTime = 0L

        collect { value ->
            lastValue = value
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastEmissionTime >= waitMillis) {
                lastValue?.let {
                    emit(it)
                    lastEmissionTime = currentTime
                }
            }
        }
    }

fun <T> Flow<T>.retryWithExponentialBackoff(
    maxAttempts: Int = 3,
    initialDelay: Duration = 1.seconds,
    maxDelay: Duration = 10.seconds,
    factor: Double = 2.0,
): Flow<T> =
    retry(maxAttempts.toLong()) { cause ->
        var currentDelay = initialDelay
        repeat(maxAttempts) {
            delay(currentDelay)
            currentDelay =
                (currentDelay * factor)
                    .coerceAtMost(maxDelay)
        }
        true
    }

fun <T> Flow<T>.shareLatest(): Flow<T> =
    shareIn(
        CoroutineScope(Dispatchers.Default + SupervisorJob()),
        SharingStarted.WhileSubscribed(),
        replay = 1,
    )

fun <T> Flow<Result<T>>.onSuccess(action: suspend (T) -> Unit): Flow<Result<T>> =
    onEach { result ->
        result.getOrNull()?.let { action(it) }
    }

fun <T> Flow<Result<T>>.onFailure(action: suspend (Throwable) -> Unit): Flow<Result<T>> =
    onEach { result ->
        result.exceptionOrNull()?.let { action(it) }
    }

fun <T> Flow<T>.toStateFlow(
    initialValue: T,
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000),
): StateFlow<T> = stateIn(scope, started, initialValue)

fun <T> Flow<T>.toSharedFlow(
    replay: Int = 0,
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000),
): SharedFlow<T> = shareIn(scope, started, replay) 
