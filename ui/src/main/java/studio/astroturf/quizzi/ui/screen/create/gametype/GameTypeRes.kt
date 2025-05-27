package studio.astroturf.quizzi.ui.screen.create.gametype

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import studio.astroturf.quizzi.domain.model.GameType
import studio.astroturf.quizzi.ui.R

@StringRes
fun GameType.getGameTypeNameResId(): Int =
    when (name) {
        "Resistance Game" -> R.string.gametype_resistance
        "Resist To Time Game" -> R.string.gametype_resist_to_time
        else -> throw IllegalArgumentException("Unknown game type: $name")
    }

@StringRes
fun GameType.getDescriptionResId(): Int =
    when (name) {
        "Resistance Game" -> R.string.gametype_resistance_description
        "Resist To Time Game" -> R.string.gametype_resist_to_time_description
        else -> throw IllegalArgumentException("Unknown game type: $name")
    }

@DrawableRes
fun GameType.getGameTypeDrawableResId(): Int =
    when (name) {
        "Resistance Game" -> R.drawable.ic_resistance_game
        "Resist To Time Game" -> R.drawable.ic_resist_to_time_game
        else -> throw IllegalArgumentException("Unknown game type: $name")
    }
