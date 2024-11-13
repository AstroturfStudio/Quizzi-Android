package com.alicankorkmaz.quizzi.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class RoomState {
    WAITING,
    PLAYING,
    FINISHED
} 