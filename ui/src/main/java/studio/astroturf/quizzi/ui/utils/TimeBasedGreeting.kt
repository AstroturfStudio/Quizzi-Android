package studio.astroturf.quizzi.ui.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import studio.astroturf.quizzi.ui.R
import java.util.Calendar

data class TimeBasedGreeting(
    @StringRes val greetingRes: Int,
    @DrawableRes val iconRes: Int,
)

object TimeBasedGreetingProvider {
    fun getCurrentGreeting(): TimeBasedGreeting {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 ->
                TimeBasedGreeting(
                    greetingRes = R.string.good_morning,
                    iconRes = R.drawable.ic_sun,
                )
            in 12..16 ->
                TimeBasedGreeting(
                    greetingRes = R.string.good_afternoon,
                    iconRes = R.drawable.ic_sun_cloud,
                )
            in 17..21 ->
                TimeBasedGreeting(
                    greetingRes = R.string.good_evening,
                    iconRes = R.drawable.ic_sun_cloud,
                )
            else ->
                TimeBasedGreeting(
                    greetingRes = R.string.good_night,
                    iconRes = R.drawable.ic_moon,
                )
        }
    }
} 
