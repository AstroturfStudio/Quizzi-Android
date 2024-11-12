package com.alicankorkmaz.quizzi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Flag(
    val id: Int,
    val name: String,
    val imageUrl: String
) 