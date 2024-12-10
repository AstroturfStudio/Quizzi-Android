package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException

/**
 * Interface defining the strategy for handling exceptions.
 */
interface ExceptionStrategy<E : QuizziException> {
    /**
     * Resolves the given exception into an [ExceptionResult].
     *
     * @param exception The exception to resolve.
     * @return The resolved [ExceptionResult].
     */
    fun resolve(exception: E): ExceptionResult
} 