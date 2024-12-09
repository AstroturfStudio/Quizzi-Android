package studio.astroturf.quizzi.ui.screen.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionHandler
import studio.astroturf.quizzi.domain.exceptionhandling.UiNotification
import studio.astroturf.quizzi.domain.repository.QuizziRepository
import studio.astroturf.quizzi.ui.extensions.handleQuizziResult
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel
    @Inject
    constructor(
        private val repository: QuizziRepository,
        private val exceptionHandler: ExceptionHandler,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RoomsUiState())
        val uiState = _uiState.asStateFlow()

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing = _isRefreshing.asStateFlow()

        private val _notification = MutableStateFlow<UiNotification?>(null)
        val notification = _notification.asStateFlow()

        init {
            viewModelScope.launch {
                getRooms()
            }
        }

        private suspend fun getRooms() {
            handleQuizziResult(
                result = repository.getRooms(),
                onSuccess = { rooms ->
                    _uiState.update {
                        it.copy(
                            isConnected = true,
                            rooms = rooms,
                            error = null,
                        )
                    }
                },
                exceptionHandler = exceptionHandler,
                onUiNotification = { notification ->
                    _notification.value = notification
                },
                onFatalException = { message, _ ->
                    _uiState.update {
                        it.copy(
                            isConnected = false,
                            error = message,
                        )
                    }
                },
            )
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

        fun clearNotification() {
            _notification.value = null
        }

        override fun onCleared() {
            super.onCleared()
            repository.disconnect()
        }
    }
