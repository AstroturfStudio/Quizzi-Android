package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException

/**
 * Strategy for handling [QuizziException.UnexpectedException].
 */
class UnexpectedExceptionStrategy : ExceptionStrategy<QuizziException.UnexpectedException> {
    override fun resolve(exception: QuizziException.UnexpectedException): ExceptionResult =
        ExceptionResult.Fatal("An unexpected error occurred. Please try again.", exception.cause ?: exception)
}
