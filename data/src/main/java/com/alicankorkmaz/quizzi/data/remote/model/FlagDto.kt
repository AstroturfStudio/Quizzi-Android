package com.alicankorkmaz.quizzi.data.remote.model

import com.alicankorkmaz.quizzi.domain.model.Flag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlagDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("imageUrl") val imageUrl: String
) {
    fun toFlag(): Flag {
        return Flag(
            id = id,
            name = name,
            imageUrl = imageUrl
        )
    }
}