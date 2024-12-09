package studio.astroturf.quizzi.ui.screen.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.di.DefaultDispatcher
import studio.astroturf.quizzi.domain.di.IoDispatcher
import studio.astroturf.quizzi.domain.di.MainDispatcher
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResolver
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.repository.QuizziRepository
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import studio.astroturf.quizzi.ui.extensions.resolve
import javax.inject.Inject

@HiltViewModel
class LandingViewModel
    @Inject
    constructor(
        private val repository: QuizziRepository,
        private val preferencesStorage: PreferencesStorage,
        private val exceptionResolver: ExceptionResolver,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
        @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
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
        ) = viewModelScope.launch(ioDispatcher) {
            repository.createPlayer(name, avatarUrl).resolve(
                exceptionResolver,
                onUiNotification = {
                    _notification.value = it
                },
                onFatalException = { message, _ ->
                    _uiState.update { it.copy(error = message) }
                },
            ) { player ->
                preferencesStorage.savePlayerId(player.id)
                viewModelScope.launch(mainDispatcher) {
                    _uiState.value = uiState.value.copy(playerId = player.id)
                }
            }
        }

        fun login(playerId: String) =
            viewModelScope.launch {
                repository.login(playerId).resolve(
                    exceptionResolver,
                    onUiNotification = { notification ->
                        _notification.value = notification
                    },
                    onFatalException = { message, _ ->
                        preferencesStorage.clearPlayerId()
                        viewModelScope.launch(mainDispatcher) {
                            _uiState.update {
                                it.copy(playerId = null, error = message)
                            }
                        }
                    },
                ) { player ->
                    preferencesStorage.savePlayerId(player.id)
                    viewModelScope.launch(mainDispatcher) {
                        _uiState.value = uiState.value.copy(playerId = player.id)
                    }
                }
            }

        fun clearNotification() {
            _notification.value = null
        }
    }
