package com.alicankorkmaz.quizzi.ui.screen.landing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState = _uiState.asStateFlow()

    fun createPlayer(name: String, avatarUrl: String) = viewModelScope.launch {
        repository.createPlayer(name, avatarUrl)
            .onSuccess {
                _uiState.value = uiState.value.copy(playerId = it.id)
            }
            .onFailure {
                Log.e("LandingViewModel", "createPlayer: ", it)
            }
    }

    fun login(playerId: String) = viewModelScope.launch {
        repository.login(playerId)
            .onSuccess {
                _uiState.value = uiState.value.copy(playerId = it.id)
            }
            .onFailure {
                // TODO:  
            }
    }
}