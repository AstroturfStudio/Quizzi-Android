package com.alicankorkmaz.flagquiz.data.remote.dto

import com.alicankorkmaz.flagquiz.domain.model.Flag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlagDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("flagUrl") val imageUrl: String
) {
    fun toFlag(): Flag {
        return Flag(
            id = id,
            name = name,
            imageUrl = imageUrl
        )
    }
}