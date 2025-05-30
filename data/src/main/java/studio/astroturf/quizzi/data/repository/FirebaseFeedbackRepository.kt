package studio.astroturf.quizzi.data.repository

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import studio.astroturf.quizzi.data.exceptionhandling.mapToQuizziException
import studio.astroturf.quizzi.domain.model.GameFeedback
import studio.astroturf.quizzi.domain.repository.FeedbackRepository
import studio.astroturf.quizzi.domain.result.QuizziResult
import java.util.UUID
import javax.inject.Inject

class FirebaseFeedbackRepository
    @Inject
    constructor() : FeedbackRepository {
        private val database = Firebase.database("https://astroturfstudio-quizzi-default-rtdb.$REGION.firebasedatabase.app").reference

        override suspend fun submitFeedback(feedback: GameFeedback): QuizziResult<Unit> =
            try {
                val feedbackId = UUID.randomUUID().toString()
                database
                    .child("feedback")
                    .child(feedbackId)
                    .setValue(feedback)
                    .await()

                QuizziResult.success(Unit)
            } catch (e: Exception) {
                QuizziResult.failure(e.mapToQuizziException())
            }

        companion object {
            const val REGION = "europe-west1"
        }
    }
