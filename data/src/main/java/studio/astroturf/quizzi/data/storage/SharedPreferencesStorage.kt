package studio.astroturf.quizzi.data.storage

import android.content.SharedPreferences
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import javax.inject.Inject

class SharedPreferencesStorage
    @Inject
    constructor(
        private val sharedPreferences: SharedPreferences,
    ) : PreferencesStorage {
        companion object {
            private const val KEY_PLAYER_ID = "playerId"
        }

        override fun savePlayerId(playerId: String) {
            sharedPreferences.edit().putString(KEY_PLAYER_ID, playerId).apply()
        }

        override fun savePlayerName(
            playerId: String,
            playerName: String,
        ) {
            sharedPreferences.edit().putString(playerId, playerName).apply()
        }

        override fun getPlayerName(playerId: String): String? = sharedPreferences.getString(playerId, null)

        override fun getPlayerId(): String? = sharedPreferences.getString(KEY_PLAYER_ID, null)

        override fun clearPlayerId() {
            sharedPreferences.edit().remove(KEY_PLAYER_ID).apply()
        }

        override fun saveOnboardingCompleted() {
            sharedPreferences.edit().putBoolean("onboardingCompleted", true).apply()
        }

        override fun isOnboardingCompleted(): Boolean = sharedPreferences.getBoolean("onboardingCompleted", false)
    }
