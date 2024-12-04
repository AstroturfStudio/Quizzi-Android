package studio.astroturf.quizzi

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class QuizziApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase first
        try {
            FirebaseApp.initializeApp(this)?.let {
                Timber.d("Firebase initialized successfully")
            } ?: run {
                Timber.e("Firebase initialization returned null")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Firebase")
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
