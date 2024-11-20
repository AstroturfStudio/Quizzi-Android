import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Preview(
    name = "Light Mode",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreview

@Preview(
    name = "Phone",
    device = "spec:width=360dp,height=640dp,dpi=480"
)
@Preview(
    name = "Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=480"
)
@Preview(
    name = "Foldable",
    device = "spec:width=673dp,height=841dp,dpi=480"
)
annotation class DevicePreview

@Preview(
    name = "English",
    locale = "en"
)
@Preview(
    name = "Turkish",
    locale = "tr"
)
annotation class LocalePreview

class FontScalePreviewParameterProvider : PreviewParameterProvider<Float> {
    override val values = sequenceOf(0.85f, 1f, 1.15f, 1.3f)
}

@Preview(
    name = "Font Scale Preview",
    group = "Accessibility"
)
@Composable
fun FontScalePreview(
    @PreviewParameter(FontScalePreviewParameterProvider::class) fontScale: Float
) {
    // Your composable here
} 