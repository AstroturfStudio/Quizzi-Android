package studio.astroturf.quizzi.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    private val mainDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    // Launch operations for suspend functions
    protected fun launchMain(block: suspend () -> Unit) {
        viewModelScope.launch(mainDispatcher) { block() }
    }

    protected fun launchIO(block: suspend () -> Unit) {
        viewModelScope.launch(ioDispatcher) { block() }
    }

    protected fun launchDefault(block: suspend () -> Unit) {
        viewModelScope.launch(defaultDispatcher) { block() }
    }
}
