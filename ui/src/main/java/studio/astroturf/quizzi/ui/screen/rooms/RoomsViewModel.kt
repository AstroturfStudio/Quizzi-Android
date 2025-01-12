package studio.astroturf.quizzi.ui.screen.rooms

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.di.DefaultDispatcher
import studio.astroturf.quizzi.domain.di.IoDispatcher
import studio.astroturf.quizzi.domain.di.MainDispatcher
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResolver
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.repository.RoomsRepository
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import studio.astroturf.quizzi.ui.base.BaseViewModel
import studio.astroturf.quizzi.ui.extensions.resolve
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel
    @Inject
    constructor(
        private val repository: RoomsRepository,
        private val exceptionResolver: ExceptionResolver,
        @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
        preferencesStorage: PreferencesStorage,
    ) : BaseViewModel(
            mainDispatcher,
            ioDispatcher,
            defaultDispatcher,
        ) {
        private val _uiState = MutableStateFlow(RoomsUiState())
        val uiState = _uiState.asStateFlow()

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing = _isRefreshing.asStateFlow()

        private val _notification = MutableStateFlow<UiNotification?>(null)
        val notification = _notification.asStateFlow()

        init {
            preferencesStorage.getPlayerId()?.let { playerId ->
                preferencesStorage.getPlayerName(playerId)?.let { playerName ->
                    updateUiState {
                        it.copy(currentUsername = playerName)
                    }
                }
            }

            viewModelScope.launch(ioDispatcher) {
                getRooms()
            }
        }

        private suspend fun getRooms() {
            repository
                .getRooms()
                .resolve(
                    exceptionResolver,
                    onUiNotification = { notification ->
                        _notification.value = notification
                    },
                    onFatalException = { message, _ ->
                        updateUiState {
                            it.copy(
                                isConnected = false,
                                error = message,
                            )
                        }
                    },
                ) { rooms ->
                    updateUiState {
                        it.copy(
                            isConnected = true,
                            rooms = rooms,
                            error = null,
                        )
                    }
                }
        }

        fun refresh() {
            viewModelScope.launch {
                try {
                    _isRefreshing.value = true
                    getRooms()
                } finally {
                    _isRefreshing.value = false
                }
            }
        }

        private fun updateUiState(newState: (RoomsUiState) -> RoomsUiState) {
            launchMain {
                _uiState.value = newState(_uiState.value)
            }
        }

        fun clearNotification() {
            _notification.value = null
        }
    }
