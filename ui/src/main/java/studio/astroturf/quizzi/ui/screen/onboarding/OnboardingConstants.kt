package studio.astroturf.quizzi.ui.screen.onboarding

import studio.astroturf.quizzi.ui.R

object OnboardingConstants {
    data class OnboardingPage(
        val title: String,
        val imageRes: Int,
    )

    val onboardingPages =
        listOf(
            OnboardingPage(
                title = "Create gamified quizzes becomes simple",
                imageRes = R.drawable.illustration_onboarding_1,
            ),
            OnboardingPage(
                title = "Find quizzes to test out your knowledge",
                imageRes = R.drawable.illustration_onboarding_2,
            ),
            OnboardingPage(
                title = "Take part in challenges with friends",
                imageRes = R.drawable.illustration_onboarding_3,
            ),
        )
}
