package com.alicankorkmaz.flagquiz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val id: Int,
    val question: String,
    val correctFlag: Flag,
    val options: List<Flag>
) 