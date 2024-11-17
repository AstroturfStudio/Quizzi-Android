package com.astroturf.quizzi.data.remote.websocket.model

import com.astroturf.quizzi.domain.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: String,
    val name: String,
    val avatarUrl: String
) {
    fun toDomain() = Player(
        id = id,
        name = name,
        avatarUrl = avatarUrl
    )
}