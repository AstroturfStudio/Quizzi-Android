package studio.astroturf.quizzi.ui.screen.game.composables

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import timber.log.Timber

val categoryIdPrefixMap = mapOf(
    1 to "flags",
    2 to "flags",
    3 to "hollywood",
    4 to "movieposters",
    5 to "footballclublogos",
)

@Composable
fun CachedQuestionImage(
    categoryId: Int,
    imageCode: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Generate the resource ID for the corresponding drawable
    val resourceName = "${categoryIdPrefixMap[categoryId]}_$imageCode"

    val resourceId =
        context.resources.getIdentifier(
            resourceName,
            "drawable",
            context.packageName,
        )

    // If resource is not found, throw exception with country info
    if (resourceId == 0) {
        Timber.tag("CachedQuestionImage").e("Resource not found for: $resourceName")
        throw Resources.NotFoundException(
            "Missing country image resource for country code: $imageCode. " +
                "Please add a drawable resource named $imageCode",
        )
    }

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = "Question Image",
        modifier = modifier,
    )
}

@Preview
@Composable
fun CachedQuestionImagePreview() {
    CachedQuestionImage(
        categoryId = 1,
        imageCode = "us",
    )
}