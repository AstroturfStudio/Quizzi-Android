package com.alicankorkmaz.quizzi.ui.screen.landing

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import com.alicankorkmaz.quizzi.domain.storage.PreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val repository: QuizRepository,
    private val preferencesStorage: PreferencesStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            savedPlayerId = preferencesStorage.getPlayerId()
        )
    }

    fun createPlayer(name: String, avatarUrl: String) = viewModelScope.launch {
        repository.createPlayer(name, avatarUrl)
            .onSuccess { player ->
                preferencesStorage.savePlayerId(player.id)
                _uiState.value = uiState.value.copy(playerId = player.id)
            }
            .onFailure {
                Log.e("LandingViewModel", "createPlayer: ", it)
            }
    }

    fun login(playerId: String) = viewModelScope.launch {
        repository.login(playerId)
            .onSuccess { player ->
                preferencesStorage.savePlayerId(player.id)
                _uiState.value = uiState.value.copy(playerId = player.id)
            }
            .onFailure {
                _uiState.value = uiState.value.copy(
                    error = "Failed to login. Please try again."
                )
            }
    }
}