package studio.astroturf.quizzi.domain.exceptionhandling

interface ExceptionResolver {
    fun resolve(exception: Exception): ExceptionResult
}

sealed class ExceptionResult {
    data class Notification(
        val notification: UiNotification,
    ) : ExceptionResult()

    data class Silent(
        val logMessage: String,
    ) : ExceptionResult()

    data class Fatal(
        val message: String,
        val throwable: Throwable,
    ) : ExceptionResult()
}
