package studio.astroturf.quizzi.data.result

import retrofit2.HttpException
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.result.QuizziResult
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> Result<T>.toQuizziResult(): QuizziResult<T> =
    fold(
        onSuccess = { QuizziResult.success(it) },
        onFailure = { throwable ->
            QuizziResult.failure(
                when (throwable) {
                    is UnknownHostException ->
                        QuizziException.HttpException(
                            message = "No internet connection",
                            cause = throwable,
                        )
                    is SocketTimeoutException ->
                        QuizziException.HttpException(
                            message = "Connection timed out",
                            cause = throwable,
                        )
                    is IOException ->
                        QuizziException.HttpException(
                            message = "Network error occurred",
                            cause = throwable,
                        )
                    is HttpException ->
                        QuizziException.HttpException(
                            message = throwable.message(),
                            code = throwable.code(),
                            cause = throwable,
                        )
                    else ->
                        QuizziException.UnexpectedException(
                            message = throwable.message ?: "An unexpected error occurred",
                            cause = throwable,
                        )
                },
            )
        },
    )
