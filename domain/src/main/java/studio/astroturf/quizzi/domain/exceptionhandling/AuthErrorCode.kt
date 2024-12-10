package studio.astroturf.quizzi.domain.exceptionhandling

enum class AuthErrorCode {
    INVALID_CREDENTIALS,
    SESSION_EXPIRED,
    UNAUTHORIZED,
    USER_NOT_FOUND,
    UNKNOWN,
}
