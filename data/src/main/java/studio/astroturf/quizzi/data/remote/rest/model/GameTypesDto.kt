package studio.astroturf.quizzi.data.remote.rest.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameTypesDto(
    @SerialName("gameTypes")
    val gameTypes: List<String>
) 