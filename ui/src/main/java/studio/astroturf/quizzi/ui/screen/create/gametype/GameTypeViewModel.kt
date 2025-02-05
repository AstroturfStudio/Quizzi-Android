package studio.astroturf.quizzi.ui.screen.create.gametype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import studio.astroturf.quizzi.domain.model.GameType
import studio.astroturf.quizzi.domain.repository.GameTypeRepository
import studio.astroturf.quizzi.domain.result.onFailure
import studio.astroturf.quizzi.domain.result.onSuccess
import javax.inject.Inject

@HiltViewModel
class GameTypeViewModel
    @Inject
    constructor(
        private val gameTypeRepository: GameTypeRepository,
    ) : ViewModel() {
        private val _gameTypesUiModel = MutableStateFlow<List<GameTypeUiModel>>(emptyList())
        val gameTypesUiModel = _gameTypesUiModel.asStateFlow()

        init {
            getGameTypes()
        }

        fun getGameTypes() =
            viewModelScope.launch {
                gameTypeRepository
                    .getGameTypes()
                    .onSuccess { gameTypes ->
                        _gameTypesUiModel.value = gameTypes.map { GameTypeUiModel.from(it) }
                    }.onFailure { error ->
                        // TODO:
                    }
            }

        /**
         * toggles the selection of the game type in the list,
         * and deselects all other game types
         */
        fun selectGameType(gameType: GameTypeUiModel) {
            _gameTypesUiModel.value =
                _gameTypesUiModel.value.map {
                    it.copy(isSelected = if (it == gameType) !it.isSelected else false)
                }
        }

        fun getSelectedGameType(): GameType? = _gameTypesUiModel.value.find { it.isSelected }?.gameType
    }
