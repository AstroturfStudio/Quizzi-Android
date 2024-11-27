package studio.astroturf.quizzi.ui.screen.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.repository.QuizRepository
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel
    @Inject
    constructor(
        private val repository: QuizRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RoomsUiState())
        val uiState = _uiState.asStateFlow()

        private var _isRefreshing = MutableStateFlow(false)
        val isRefreshing = _isRefreshing.asStateFlow()

        init {
            viewModelScope.launch {
                getRooms()
            }
        }

        private suspend fun getRooms() {
            repository
                .getRooms()
                .onSuccess { rooms ->
                    _uiState.update {
                        it.copy(
                            isConnected = true,
                            rooms = rooms,
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isConnected = false,
                            error = error.message,
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

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
