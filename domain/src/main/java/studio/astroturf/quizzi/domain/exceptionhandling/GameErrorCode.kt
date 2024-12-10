package studio.astroturf.quizzi.domain.exceptionhandling

enum class GameErrorCode {
    ROOM_NOT_FOUND,
    GAME_ALREADY_STARTED,
    INVALID_GAME_STATE,
    PLAYER_NOT_FOUND,
    ANSWER_ALREADY_SUBMITTED,
    UNKNOWN,
}
