package com.astroturf.quizzi.domain.model

data class Question(
    val categoryId: Int,
    val imageUrl: String?,
    val content: String,
    val options: List<Option>
)