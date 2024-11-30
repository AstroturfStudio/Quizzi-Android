package studio.astroturf.quizzi.ui.screen.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.repository.QuizRepository
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LandingViewModel
    @Inject
    constructor(
        private val repository: QuizRepository,
        private val preferencesStorage: PreferencesStorage,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LandingUiState())
        val uiState = _uiState.asStateFlow()

        init {
            preferencesStorage.getPlayerId()?.let {
                login(it)
            }
        }

        fun createPlayer(
            name: String,
            avatarUrl: String,
        ) = viewModelScope.launch {
            repository
                .createPlayer(name, avatarUrl)
                .onSuccess { player ->
                    preferencesStorage.savePlayerId(player.id)
                    _uiState.value = uiState.value.copy(playerId = player.id)
                }.onFailure { err ->
                    Timber.tag("LandingViewModel").e(err, "createPlayer: ")
                    _uiState.update {
                        it.copy(error = err.message)
                    }
                }
        }

        fun login(playerId: String) =
            viewModelScope.launch {
                repository
                    .login(playerId)
                    .onSuccess { player ->
                        preferencesStorage.savePlayerId(player.id)
                        _uiState.value = uiState.value.copy(playerId = player.id)
                    }.onFailure { err ->
                        Timber.tag("LandingViewModel").e(err, "Failed to login with playerId = $playerId")
                        preferencesStorage.clearPlayerId()
                        _uiState.update {
                            it.copy(playerId = null, error = err.message)
                        }
                    }
            }
    }
