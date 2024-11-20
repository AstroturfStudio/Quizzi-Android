package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlayerRequestDto(
    val name: String,
    val avatarUrl: String
) 