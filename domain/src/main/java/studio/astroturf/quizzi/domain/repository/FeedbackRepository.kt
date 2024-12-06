package studio.astroturf.quizzi.domain.repository

import studio.astroturf.quizzi.domain.model.GameFeedback
import studio.astroturf.quizzi.domain.result.QuizziResult

interface FeedbackRepository {
    suspend fun submitFeedback(feedback: GameFeedback): QuizziResult<Unit>
}
