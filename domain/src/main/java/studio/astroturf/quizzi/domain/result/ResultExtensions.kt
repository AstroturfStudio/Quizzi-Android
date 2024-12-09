package studio.astroturf.quizzi.domain.result

import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException

/**
 * Executes the corresponding lambda based on the [QuizziResult] state.
 *
 * @param onSuccess Lambda to execute if the result is [Success].
 * @param onFailure Lambda to execute if the result is [Failure].
 */
fun <T> QuizziResult<T>.fold(
    onSuccess: (T) -> Unit,
    onFailure: (QuizziException) -> Unit,
) {
    when (this) {
        is QuizziResult.Success -> onSuccess(value)
        is QuizziResult.Failure -> onFailure(exception)
    }
}

/**
 * Transforms the [QuizziResult] value using the provided [transform] function if it's a [Success].
 *
 * @param transform Function to apply to the [Success] value.
 * @return A new [QuizziResult] containing the transformed value or the original exception.
 */
fun <T, R> QuizziResult<T>.map(transform: (T) -> R): QuizziResult<R> =
    when (this) {
        is QuizziResult.Success -> QuizziResult.success(transform(value))
        is QuizziResult.Failure -> QuizziResult.failure(exception)
    }

/**
 * Returns the [Success] value or `null` if it's a [Failure].
 *
 * @return The [Success] value or `null`.
 */
fun <T> QuizziResult<T>.getOrNull(): T? =
    when (this) {
        is QuizziResult.Success -> value
        is QuizziResult.Failure -> null
    }

/**
 * Returns the [Success] value or throws the contained [QuizziException] if it's a [Failure].
 *
 * @return The [Success] value.
 * @throws QuizziException if the result is a [Failure].
 */
fun <T> QuizziResult<T>.getOrThrow(): T =
    when (this) {
        is QuizziResult.Success -> value
        is QuizziResult.Failure -> throw exception
    }

/**
 * Performs the given [action] if the [QuizziResult] is a [Success].
 *
 * @param action The action to perform on success.
 * @return The original [QuizziResult] for chaining.
 */
inline fun <T> QuizziResult<T>.onSuccess(action: (value: T) -> Unit): QuizziResult<T> {
    if (this is QuizziResult.Success) {
        action(this.value)
    }
    return this
}

/**
 * Performs the given [action] if the [QuizziResult] is a [Failure].
 *
 * @param action The action to perform on failure.
 * @return The original [QuizziResult] for chaining.
 */
inline fun <T> QuizziResult<T>.onFailure(action: (exception: QuizziException) -> Unit): QuizziResult<T> {
    if (this is QuizziResult.Failure) {
        action(this.exception)
    }
    return this
}

/**
 * Transforms the [QuizziResult] into another type based on success or failure.
 *
 * @param onSuccess Transformation to apply on success.
 * @param onFailure Transformation to apply on failure.
 * @return A new [QuizziResult] with the transformed value or exception.
 */
inline fun <T, R> QuizziResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: QuizziException) -> R,
): R =
    when (this) {
        is QuizziResult.Success -> onSuccess(this.value)
        is QuizziResult.Failure -> onFailure(this.exception)
    }
