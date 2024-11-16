package com.alicankorkmaz.quizzi.data.storage

import android.content.SharedPreferences
import com.alicankorkmaz.quizzi.domain.storage.PreferencesStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferencesStorage {

    companion object {
        private const val KEY_PLAYER_ID = "playerId"
    }

    override fun savePlayerId(playerId: String) {
        sharedPreferences.edit().putString(KEY_PLAYER_ID, playerId).apply()
    }

    override fun getPlayerId(): String? {
        return sharedPreferences.getString(KEY_PLAYER_ID, null)
    }

    override fun clearPlayerId() {
        sharedPreferences.edit().remove(KEY_PLAYER_ID).apply()
    }
} 