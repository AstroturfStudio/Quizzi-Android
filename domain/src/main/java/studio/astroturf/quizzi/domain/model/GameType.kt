package studio.astroturf.quizzi.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameType(
    val name: String,
) : Parcelable
