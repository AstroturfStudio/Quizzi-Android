import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> withTimeout(
    timeMillis: Long,
    defaultValue: T,
    block: suspend () -> T,
): T =
    try {
        kotlinx.coroutines.withTimeout(timeMillis) { block() }
    } catch (e: TimeoutCancellationException) {
        defaultValue
    }

fun CoroutineScope.launchPeriodicAsync(
    delayMillis: Long,
    initialDelayMillis: Long = 0L,
    action: suspend () -> Unit,
) = async {
    delay(initialDelayMillis)
    while (isActive) {
        action()
        delay(delayMillis)
    }
}

suspend fun <T> retryWithExponentialBackoff(
    times: Int = 3,
    initialDelayMillis: Long = 100,
    maxDelayMillis: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelayMillis
    repeat(times - 1) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
        }
    }
    return block() // last attempt
}

fun <T> Flow<T>.throttleLatest(
    windowDuration: Long = 1000L,
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
): Flow<T> =
    channelFlow {
        val events = Channel<T>(Channel.CONFLATED)

        launch(context) {
            var lastValue: T? = null
            while (isActive) {
                delay(windowDuration)
                lastValue?.let { send(it) }
                lastValue = null
            }
        }

        collect { value ->
            events.send(value)
        }
    }.flowOn(context)

suspend fun <T> withRetry(
    times: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
    return block()
} 
