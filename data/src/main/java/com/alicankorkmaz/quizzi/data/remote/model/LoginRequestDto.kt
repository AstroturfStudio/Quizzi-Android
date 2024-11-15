package com.alicankorkmaz.quizzi.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val id: String
)