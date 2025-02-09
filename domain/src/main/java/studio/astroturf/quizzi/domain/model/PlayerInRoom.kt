package studio.astroturf.quizzi.domain.model

data class PlayerInRoom(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val state: PlayerState,
) {
    val isReady: Boolean
        get() = state == PlayerState.READY
}

enum class PlayerState {
    WAIT,
    READY,
}
