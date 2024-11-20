package studio.astroturf.quizzi.domain.storage

interface PreferencesStorage {
    fun savePlayerId(playerId: String)
    fun getPlayerId(): String?
    fun clearPlayerId()
}