package studio.astroturf.quizzi.domain.result

import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException

/**
 * Represents the result of an operation that may fail with a [QuizziException].
 *
 * @param T The type of successful value
 */
sealed interface QuizziResult<out T> {
    /** Represents a successful operation with [value] */
    data class Success<T>(
        val value: T,
    ) : QuizziResult<T>

    /** Represents a failed operation with [exception] */
    data class Failure(
        val exception: QuizziException,
    ) : QuizziResult<Nothing>

    companion object {
        /** Creates a successful result containing [value] */
        fun <T> success(value: T): QuizziResult<T> = Success(value)

        /** Creates a failed result containing [exception] */
        fun failure(exception: QuizziException): QuizziResult<Nothing> = Failure(exception)
    }
}
