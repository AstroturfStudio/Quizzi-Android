package studio.astroturf.quizzi.domain.model

data class GameStatistics(
    val roundCount: Int,
    val averageResponseTimeMillis: Map<Player, Long>,
    val totalGameLengthMillis: Long,
)
