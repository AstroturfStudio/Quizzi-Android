package com.alicankorkmaz.quizzi.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlayerRequestDto(
    val name: String,
    val avatarUrl: String
) 