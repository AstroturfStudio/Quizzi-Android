package studio.astroturf.quizzi.domain.repository

import studio.astroturf.quizzi.domain.model.GameFeedback

interface FeedbackRepository {
    suspend fun submitFeedback(feedback: GameFeedback)
}
