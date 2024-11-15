package com.alicankorkmaz.quizzi.data.remote.model

import com.alicankorkmaz.quizzi.domain.model.GameRoom
import com.alicankorkmaz.quizzi.domain.model.RoomState
import kotlinx.serialization.Serializable

@Serializable
data class RoomsDto(
    val rooms: List<GameRoomDto>
)

@Serializable
data class GameRoomDto(
    val id: String,
    val playerCount: Int,
    val roomState: RoomState,
    val players: List<String> // oyuncu isimleri
) {
    fun toDomain() = GameRoom(
        id = id,
        playerCount = playerCount,
        roomState = roomState,
        players = players
    )
}