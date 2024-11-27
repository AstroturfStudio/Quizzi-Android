import android.content.Context
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Context.dpToPx(dp: Float): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics,
    )

fun Context.spToPx(sp: Float): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        resources.displayMetrics,
    )

@Composable
fun Int.pxToDp(): Dp =
    with(LocalDensity.current) {
        this@pxToDp.toDp()
    }

@Composable
fun Float.pxToDp(): Dp =
    with(LocalDensity.current) {
        this@pxToDp.toDp()
    }

@Composable
fun Dp.toPx(): Float =
    with(LocalDensity.current) {
        this@toPx.toPx()
    }
