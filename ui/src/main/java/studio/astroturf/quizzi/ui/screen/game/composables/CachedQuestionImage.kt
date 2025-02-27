package studio.astroturf.quizzi.ui.screen.game.composables

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import timber.log.Timber

@Composable
fun CachedQuestionImage(
    countryCode: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Generate the resource ID for the corresponding drawable
    val resourceName = "${countryCode}_${IMAGE_WIDTH_PX_LARGE}"
    val resourceId =
        context.resources.getIdentifier(
            resourceName,
            "drawable",
            context.packageName,
        )

    // If resource is not found, try the small version
    if (resourceId == 0) {
        Timber.tag("CachedQuestionImage").e("Resource not found for: $resourceName")

        // Try to use the small version instead
        val smallResourceName = "${countryCode}_${IMAGE_WIDTH_PX_SMALL}"
        val smallResourceId =
            context.resources.getIdentifier(
                smallResourceName,
                "drawable",
                context.packageName,
            )

        // If we still don't have a valid resource, throw exception with country info
        if (smallResourceId == 0) {
            Timber.tag("CachedQuestionImage").e("No images found for country: $countryCode")
            throw Resources.NotFoundException(
                "Missing country image resources for country code: $countryCode. " +
                    "Please add drawable resources named ${countryCode}_320 and/or ${countryCode}_160",
            )
        }

        // Use small image if it exists
        Image(
            painter = painterResource(id = smallResourceId),
            contentDescription = "Question Image (Small)",
            modifier = modifier,
        )
    } else {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = "Question Image",
            modifier = modifier,
        )
    }
}

private const val IMAGE_WIDTH_PX_LARGE = 320
private const val IMAGE_WIDTH_PX_SMALL = 160
