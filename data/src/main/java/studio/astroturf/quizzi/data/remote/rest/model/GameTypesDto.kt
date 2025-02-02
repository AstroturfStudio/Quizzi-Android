package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameTypesDto(
    @SerialName("types")
    val gameTypes: List<String>,
)
