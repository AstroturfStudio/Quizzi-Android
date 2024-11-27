package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val id: String,
)
