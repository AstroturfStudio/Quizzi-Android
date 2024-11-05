package com.alicankorkmaz.flagquiz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Flag(
    val id: Int,
    val name: String,
    val imageUrl: String
) 