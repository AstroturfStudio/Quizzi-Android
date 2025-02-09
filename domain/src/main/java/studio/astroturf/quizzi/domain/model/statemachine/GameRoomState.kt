package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.PlayerInRoom

sealed interface GameRoomState {
    object Idle : GameRoomState

    data class Waiting(
        val players: List<PlayerInRoom>,
    ) : GameRoomState

    object Countdown : GameRoomState

    data class Playing(
        val players: List<PlayerInRoom>,
    ) : GameRoomState

    object Paused : GameRoomState

    data class Closed(
        val players: List<PlayerInRoom>,
    ) : GameRoomState
}
