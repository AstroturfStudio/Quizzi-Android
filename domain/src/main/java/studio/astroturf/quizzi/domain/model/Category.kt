package studio.astroturf.quizzi.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class CategoryId(
    val id: Int,
) {
    COUNTRY_FLAGS(1),
    COUNTRY_CAPITALS(2),
    HOLLYWOOD_STARS(3),
    MOVIE_POSTERS(4),
    FOOTBALL_CLUB_LOGOS(5),
    ;

    companion object {
        fun fromId(id: Int): CategoryId =
            CategoryId.entries.find { it.id == id } ?: throw IllegalArgumentException("Invalid category id: $id")
    }
}

@Parcelize
data class Category(
    val id: CategoryId,
    val name: String,
) : Parcelable
