package studio.astroturf.quizzi.domain.exceptionhandling

sealed interface UiNotification {
    val message: String

    data class Toast(
        override val message: String,
        val duration: Duration = Duration.SHORT,
    ) : UiNotification

    data class Snackbar(
        override val message: String,
        val action: SnackbarAction? = null,
        val duration: Duration = Duration.SHORT,
    ) : UiNotification

    data class Dialog(
        override val message: String,
        val title: String? = null,
        val primaryAction: DialogAction? = null,
        val secondaryAction: DialogAction? = null,
        val isDismissable: Boolean = true,
    ) : UiNotification

    enum class Duration {
        SHORT,
        LONG,
    }
}

data class SnackbarAction(
    val label: String,
    val action: () -> Unit,
)

data class DialogAction(
    val label: String,
    val style: DialogActionStyle = DialogActionStyle.DEFAULT,
    val action: () -> Unit,
) {
    enum class DialogActionStyle {
        DEFAULT,
        DESTRUCTIVE,
        EMPHASIZED,
    }
}
