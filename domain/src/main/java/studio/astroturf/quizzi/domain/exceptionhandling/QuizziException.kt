package studio.astroturf.quizzi.domain.exceptionhandling

sealed class QuizziException : Exception() {
    data class HttpException(
        override val message: String,
        val code: Int? = null,
        override val cause: Throwable? = null,
    ) : QuizziException()

    data class AuthException(
        override val message: String,
        val userId: String? = null,
        val errorCode: AuthErrorCode? = null,
    ) : QuizziException()

    data class WebSocketException(
        override val message: String,
        val code: WebSocketErrorCode? = null,
        override val cause: Throwable? = null,
    ) : QuizziException()

    data class GameException(
        override val message: String,
        val gameId: String? = null,
        val errorCode: GameErrorCode? = null,
    ) : QuizziException()

    data class UnexpectedException(
        override val message: String,
        override val cause: Throwable? = null,
    ) : QuizziException()
}
