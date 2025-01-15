package studio.astroturf.quizzi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import studio.astroturf.quizzi.ui.QuizziApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesStorage: PreferencesStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizziApp(
                onBoardingCompleted = preferencesStorage.isOnboardingCompleted(),
            )
        }
    }
}
