package studio.astroturf.quizzi.ui.exceptionhandling

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import showToast
import studio.astroturf.quizzi.domain.exceptionhandling.DialogAction
import studio.astroturf.quizzi.domain.exceptionhandling.SnackbarAction
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification

@Composable
fun NotificationHandler(
    notification: UiNotification?,
    onDismiss: () -> Unit,
) {
    notification?.let { currentNotification ->
        when (currentNotification) {
            is UiNotification.Toast -> {
                QuizziToast(
                    message = currentNotification.message,
                    duration = currentNotification.duration,
                    onDismiss = onDismiss,
                )
            }
            is UiNotification.Snackbar -> {
                QuizziSnackbar(
                    message = currentNotification.message,
                    action = currentNotification.action,
                    duration = currentNotification.duration,
                    onDismiss = onDismiss,
                )
            }
            is UiNotification.Dialog -> {
                QuizziDialog(
                    title = currentNotification.title,
                    message = currentNotification.message,
                    primaryAction = currentNotification.primaryAction,
                    secondaryAction = currentNotification.secondaryAction,
                    isDismissable = currentNotification.isDismissable,
                    onDismiss = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun QuizziToast(
    message: String,
    duration: UiNotification.Duration,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    LaunchedEffect(message) {
        context.showToast(
            message = message,
            duration =
                when (duration) {
                    UiNotification.Duration.SHORT -> android.widget.Toast.LENGTH_SHORT
                    UiNotification.Duration.LONG -> android.widget.Toast.LENGTH_LONG
                },
        )
        currentOnDismiss()
    }
}

@Composable
private fun QuizziSnackbar(
    message: String,
    action: SnackbarAction?,
    duration: UiNotification.Duration,
    onDismiss: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentAction by rememberUpdatedState(action)

    LaunchedEffect(message) {
        val result =
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = currentAction?.label,
                duration =
                    when (duration) {
                        UiNotification.Duration.SHORT -> SnackbarDuration.Short
                        UiNotification.Duration.LONG -> SnackbarDuration.Long
                    },
            )

        when (result) {
            SnackbarResult.ActionPerformed -> currentAction?.action?.invoke()
            SnackbarResult.Dismissed -> {}
        }
        currentOnDismiss()
    }

    SnackbarHost(hostState = snackbarHostState)
}

@Composable
private fun QuizziDialog(
    title: String?,
    message: String,
    primaryAction: DialogAction?,
    secondaryAction: DialogAction?,
    isDismissable: Boolean,
    onDismiss: () -> Unit,
) {
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentPrimaryAction by rememberUpdatedState(primaryAction)
    val currentSecondaryAction by rememberUpdatedState(secondaryAction)

    AlertDialog(
        onDismissRequest = { if (isDismissable) currentOnDismiss() },
        title = title?.let { { Text(it) } },
        text = { Text(message) },
        confirmButton = {
            currentPrimaryAction?.let { action ->
                Button(
                    onClick = {
                        action.action()
                        currentOnDismiss()
                    },
                ) {
                    Text(action.label)
                }
            }
        },
        dismissButton = {
            currentSecondaryAction?.let { action ->
                TextButton(
                    onClick = {
                        action.action()
                        currentOnDismiss()
                    },
                ) {
                    Text(action.label)
                }
            }
        },
    )
}
