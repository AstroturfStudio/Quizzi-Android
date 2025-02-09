package studio.astroturf.quizzi.ui.screen.rooms

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        private var periodicGetRoomsJob: Job? = null
        private val jobMutex = Mutex() // Add this for thread safety

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

            startPeriodicRoomUpdates()
        }

        fun startPeriodicRoomUpdates() {
            launchIO {
                jobMutex.withLock {
                    // Only start if not already running
                    if (periodicGetRoomsJob?.isActive != true) {
                        periodicGetRoomsJob =
                            viewModelScope.launch(ioDispatcher) {
                                while (isActive) {
                                    getRooms()
                                    delay(PERIODIC_ROOMS_UPDATE_MS)
                                }
                            }
                    }
                }
            }
        }

        fun stopPeriodicRequests() {
            launchIO {
                jobMutex.withLock {
                    periodicGetRoomsJob?.let { job ->
                        if (job.isActive) {
                            job.cancel()
                        }
                        periodicGetRoomsJob = null
                    }
                }
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
                    updateUiState { currentState ->
                        currentState.copy(
                            isConnected = true,
                            rooms = rooms,
                            filteredRooms =
                                if (currentState.searchText.isBlank()) {
                                    rooms
                                } else {
                                    rooms.filter { room ->
                                        room.players
                                            .firstOrNull()
                                            ?.contains(currentState.searchText, ignoreCase = true) ?: false
                                    }
                                },
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

        fun onSearch(query: String) {
            updateUiState { currentState ->
                currentState.copy(
                    searchText = query,
                    filteredRooms =
                        if (query.isBlank()) {
                            currentState.rooms
                        } else {
                            currentState.rooms.filter { room ->
                                room.players.firstOrNull()?.contains(query, ignoreCase = true) ?: false
                            }
                        },
                )
            }
        }

        fun clearSearch() {
            updateUiState { currentState ->
                currentState.copy(
                    searchText = "",
                    filteredRooms = currentState.rooms,
                )
            }
        }

        override fun onCleared() {
            super.onCleared()
            stopPeriodicRequests()
        }

        companion object {
            private const val PERIODIC_ROOMS_UPDATE_MS = 5_000L
        }
    }
