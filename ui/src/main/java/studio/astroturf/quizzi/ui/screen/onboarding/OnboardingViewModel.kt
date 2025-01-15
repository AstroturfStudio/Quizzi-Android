package studio.astroturf.quizzi.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val preferencesStorage: PreferencesStorage,
    ) : ViewModel() {
        fun completeOnboarding() {
            preferencesStorage.saveOnboardingCompleted()
        }
    }
