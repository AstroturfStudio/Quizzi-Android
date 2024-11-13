package com.alicankorkmaz.quizzi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ClientQuestion(
    val id: Int,
    val imageUrl: String?,
    val content: String,
    val options: List<Option>
)

@Serializable
data class Option(
    val id: Int,
    val value: String
)