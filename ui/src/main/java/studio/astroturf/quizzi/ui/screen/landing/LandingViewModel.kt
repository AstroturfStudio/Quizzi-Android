package studio.astroturf.quizzi.ui.screen.landing

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import studio.astroturf.quizzi.domain.di.DefaultDispatcher
import studio.astroturf.quizzi.domain.di.IoDispatcher
import studio.astroturf.quizzi.domain.di.MainDispatcher
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResolver
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.repository.AuthRepository
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import studio.astroturf.quizzi.ui.base.BaseViewModel
import studio.astroturf.quizzi.ui.extensions.resolve
import javax.inject.Inject

@HiltViewModel
class LandingViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val preferencesStorage: PreferencesStorage,
        private val exceptionResolver: ExceptionResolver,
        @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    ) : BaseViewModel(
            mainDispatcher,
            ioDispatcher,
            defaultDispatcher,
        ) {
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
        ) {
            launchIO {
                authRepository
                    .createPlayer(name, avatarUrl)
                    .resolve(
                        exceptionResolver,
                        onUiNotification = {
                            _notification.value = it
                        },
                        onFatalException = { message, _ ->
                            updateUiState { it.copy(error = message) }
                        },
                    ) { player ->
                        preferencesStorage.savePlayerId(player.id)
                        preferencesStorage.savePlayerName(player.id, player.name)
                        updateUiState { it.copy(playerId = player.id) }
                    }
            }
        }

        fun login(playerId: String) {
            launchIO {
                authRepository
                    .login(playerId)
                    .resolve(
                        exceptionResolver,
                        onUiNotification = { notification ->
                            _notification.value = notification
                        },
                        onFatalException = { message, _ ->
                            preferencesStorage.clearPlayerId()
                            updateUiState { it.copy(playerId = null, error = message) }
                        },
                    ) { player ->
                        preferencesStorage.savePlayerId(player.id)
                        updateUiState { it.copy(playerId = player.id) }
                    }
            }
        }

        private fun updateUiState(newState: (LandingUiState) -> LandingUiState) {
            launchMain {
                _uiState.value = newState(_uiState.value)
            }
        }

        fun clearNotification() {
            _notification.value = null
        }
    }
