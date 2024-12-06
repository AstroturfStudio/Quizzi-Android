package studio.astroturf.quizzi.ui.screen.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionHandler
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.repository.QuizRepository
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import studio.astroturf.quizzi.ui.extensions.handleQuizziResult
import javax.inject.Inject

@HiltViewModel
class LandingViewModel
    @Inject
    constructor(
        private val repository: QuizRepository,
        private val preferencesStorage: PreferencesStorage,
        private val exceptionHandler: ExceptionHandler,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LandingUiState())
        val uiState = _uiState.asStateFlow()

        private val _notification = MutableStateFlow<UiNotification?>(null)
        val notification = _notification.asStateFlow()

        init {
            preferencesStorage.getPlayerId()?.let {
                login(it)
            }
        }

        fun createPlayer(
            name: String,
            avatarUrl: String,
        ) = viewModelScope.launch {
            handleQuizziResult(
                result = repository.createPlayer(name, avatarUrl),
                onSuccess = { player ->
                    preferencesStorage.savePlayerId(player.id)
                    _uiState.value = uiState.value.copy(playerId = player.id)
                },
                exceptionHandler = exceptionHandler,
                onUiNotification = { notification ->
                    _notification.value = notification
                },
                onFatalException = { message, _ ->
                    _uiState.update { it.copy(error = message) }
                },
            )
        }

        fun login(playerId: String) =
            viewModelScope.launch {
                handleQuizziResult(
                    result = repository.login(playerId),
                    onSuccess = { player ->
                        preferencesStorage.savePlayerId(player.id)
                        _uiState.value = uiState.value.copy(playerId = player.id)
                    },
                    exceptionHandler = exceptionHandler,
                    onUiNotification = { notification ->
                        _notification.value = notification
                    },
                    onFatalException = { message, _ ->
                        preferencesStorage.clearPlayerId()
                        _uiState.update {
                            it.copy(playerId = null, error = message)
                        }
                    },
                )
            }

        fun clearNotification() {
            _notification.value = null
        }
    }
