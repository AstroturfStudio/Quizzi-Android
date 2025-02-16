package studio.astroturf.quizzi.ui.screen.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import studio.astroturf.quizzi.ui.R

object OnboardingConstants {
    data class OnboardingPage(
        @StringRes val titleStringRes: Int,
        @DrawableRes val imageRes: Int,
    )

    val onboardingPages =
        listOf(
            OnboardingPage(
                titleStringRes = R.string.onboarding_page_1_title,
                imageRes = R.drawable.illustration_onboarding_1,
            ),
            OnboardingPage(
                titleStringRes = R.string.onboarding_page_2_title,
                imageRes = R.drawable.illustration_onboarding_2,
            ),
            OnboardingPage(
                titleStringRes = R.string.onboarding_page_3_title,
                imageRes = R.drawable.illustration_onboarding_3,
            ),
        )
}
