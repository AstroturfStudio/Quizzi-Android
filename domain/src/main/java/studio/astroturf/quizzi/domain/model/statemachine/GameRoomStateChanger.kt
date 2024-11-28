package studio.astroturf.quizzi.domain.model.statemachine

import studio.astroturf.quizzi.domain.model.websocket.ServerMessage

sealed interface GameRoomStateChanger {
    data class RoomUpdate(
        val message: ServerMessage.RoomUpdate,
    ) : GameRoomStateChanger
}
