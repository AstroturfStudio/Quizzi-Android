package studio.astroturf.quizzi.domain.storage

interface PreferencesStorage {
    fun savePlayerId(playerId: String)

    fun savePlayerName(
        playerId: String,
        playerName: String,
    )

    fun getPlayerName(playerId: String): String?

    fun getPlayerId(): String?

    fun clearPlayerId()

    fun saveOnboardingCompleted()

    fun isOnboardingCompleted(): Boolean
}
