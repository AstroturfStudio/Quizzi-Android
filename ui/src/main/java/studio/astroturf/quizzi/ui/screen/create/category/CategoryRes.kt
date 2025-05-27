package studio.astroturf.quizzi.ui.screen.create.category

import androidx.annotation.StringRes
import studio.astroturf.quizzi.domain.model.CategoryId
import studio.astroturf.quizzi.ui.R

@StringRes
fun CategoryId.getCategoryNameResId(): Int =
    when (this) {
        CategoryId.COUNTRY_FLAGS -> R.string.category_country_flags
        CategoryId.COUNTRY_CAPITALS -> R.string.category_country_capitals
        CategoryId.HOLLYWOOD_STARS -> R.string.category_hollywood_stars
        CategoryId.MOVIE_POSTERS -> R.string.category_movie_posters
        CategoryId.FOOTBALL_CLUB_LOGOS -> R.string.category_football_club_logos
    }
