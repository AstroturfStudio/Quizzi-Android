package com.alicankorkmaz.flagquiz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ClientQuestion(
    val flagUrl: String,
    val options: List<Option>,
)

@Serializable
data class Option(
    val id: String,
    val name: String
)