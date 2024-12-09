package studio.astroturf.quizzi.data.exceptionhandling

import retrofit2.HttpException
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Exception.mapToQuizziException(): QuizziException =
    when (this) {
        is UnknownHostException ->
            QuizziException.HttpException(
                message = "No internet connection",
                cause = this,
            )
        is SocketTimeoutException ->
            QuizziException.HttpException(
                message = "Connection timed out",
                cause = this,
            )
        is IOException ->
            QuizziException.HttpException(
                message = "Network error occurred",
                cause = this,
            )
        is HttpException ->
            QuizziException.HttpException(
                message = message(),
                code = code(),
                cause = this,
            )
        else ->
            QuizziException.UnexpectedException(
                message = message ?: "An unexpected error occurred",
                cause = this,
            )
    }
