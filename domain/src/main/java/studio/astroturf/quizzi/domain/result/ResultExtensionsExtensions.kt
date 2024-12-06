package studio.astroturf.quizzi.domain.result

import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException

fun <T> QuizziResult<T>.fold(
    onSuccess: (T) -> Unit,
    onFailure: (QuizziException) -> Unit,
) {
    when (this) {
        is QuizziResult.Success -> onSuccess(value)
        is QuizziResult.Failure -> onFailure(exception)
    }
}

fun <T, R> QuizziResult<T>.map(transform: (T) -> R): QuizziResult<R> =
    when (this) {
        is QuizziResult.Success -> QuizziResult.success(transform(value))
        is QuizziResult.Failure -> QuizziResult.failure(exception)
    }

fun <T> QuizziResult<T>.getOrNull(): T? =
    when (this) {
        is QuizziResult.Success -> value
        is QuizziResult.Failure -> null
    }

fun <T> QuizziResult<T>.getOrThrow(): T =
    when (this) {
        is QuizziResult.Success -> value
        is QuizziResult.Failure -> throw exception
    }
