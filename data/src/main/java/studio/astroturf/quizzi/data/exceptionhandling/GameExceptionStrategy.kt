package studio.astroturf.quizzi.data.exceptionhandling

import studio.astroturf.quizzi.domain.exceptionhandling.DialogAction
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResult
import studio.astroturf.quizzi.domain.exceptionhandling.GameErrorCode
import studio.astroturf.quizzi.domain.exceptionhandling.QuizziException
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification

/**
 * Strategy for handling [QuizziException.GameException].
 */
class GameExceptionStrategy : ExceptionStrategy<QuizziException.GameException> {
    override fun resolve(exception: QuizziException.GameException): ExceptionResult =
        when (exception.errorCode) {
            GameErrorCode.ROOM_NOT_FOUND ->
                ExceptionResult.Notification(
                    UiNotification.Dialog(
                        message = "Game room not found",
                        primaryAction = DialogAction("Return to Lobby") { /* Implement return action */ },
                        isDismissable = true,
                    ),
                )
            GameErrorCode.GAME_ALREADY_STARTED ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(message = "Game has already started"),
                )
            GameErrorCode.INVALID_GAME_STATE ->
                ExceptionResult.Silent(
                    "Invalid game state: ${exception.message}",
                )
            GameErrorCode.PLAYER_NOT_FOUND ->
                ExceptionResult.Notification(
                    UiNotification.Snackbar(message = "Player not found in game"),
                )
            GameErrorCode.ANSWER_ALREADY_SUBMITTED ->
                ExceptionResult.Notification(
                    UiNotification.Toast(message = "Answer already submitted"),
                )
            GameErrorCode.UNKNOWN ->
                ExceptionResult.Silent("Unspecified game error: ${exception.message}")
        }
}
