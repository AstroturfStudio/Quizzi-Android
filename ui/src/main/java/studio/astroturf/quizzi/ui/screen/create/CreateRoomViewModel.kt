package studio.astroturf.quizzi.ui.screen.create

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import studio.astroturf.quizzi.domain.di.DefaultDispatcher
import studio.astroturf.quizzi.domain.di.IoDispatcher
import studio.astroturf.quizzi.domain.di.MainDispatcher
import studio.astroturf.quizzi.domain.model.Category
import studio.astroturf.quizzi.domain.model.GameType
import studio.astroturf.quizzi.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CreateRoomViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    ) : BaseViewModel(
            mainDispatcher,
            ioDispatcher,
            defaultDispatcher,
        ) {
        private val _roomTitle = MutableStateFlow("")
        val roomTitle = _roomTitle.asStateFlow()

        private val _quizCategory =
            MutableStateFlow<Category?>(null)
        val quizCategory = _quizCategory.asStateFlow()

        private val _gameType = MutableStateFlow<GameType?>(null)
        val gameType = _gameType.asStateFlow()

        fun setQuizCategory(category: Category?) {
            _quizCategory.value = category
        }

        fun setGameType(gameType: GameType?) {
            _gameType.value = gameType
        }

        fun setRoomTitle(title: String) {
            _roomTitle.value = title
        }
    }
