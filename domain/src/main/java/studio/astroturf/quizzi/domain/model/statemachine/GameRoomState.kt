package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.Player

sealed interface GameRoomState {
    object Idle : GameRoomState

    data class Waiting(
        val players: List<Player>,
    ) : GameRoomState

    object Countdown : GameRoomState

    data class Playing(
        val players: List<Player>,
    ) : GameRoomState

    object Paused : GameRoomState

    data class Closed(
        val players: List<Player>,
    ) : GameRoomState
}
