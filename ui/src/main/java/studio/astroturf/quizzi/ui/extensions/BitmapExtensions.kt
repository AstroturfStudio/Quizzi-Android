import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Shader
import androidx.core.graphics.scale
import kotlin.math.min

fun Bitmap.toRoundedBitmap(): Bitmap {
    val output =
        Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888,
        )

    val canvas = Canvas(output)
    val paint =
        Paint().apply {
            isAntiAlias = true
            shader =
                BitmapShader(this@toRoundedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

    canvas.drawCircle(
        width / 2f,
        height / 2f,
        min(width, height) / 2f,
        paint,
    )

    return output
}

fun Bitmap.resize(
    maxWidth: Int,
    maxHeight: Int,
): Bitmap {
    val ratio =
        min(
            maxWidth.toFloat() / width,
            maxHeight.toFloat() / height,
        )

    return scale(
        (width * ratio).toInt(),
        (height * ratio).toInt(),
        filter = true,
    )
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix =
        Matrix().apply {
            postRotate(degrees)
        }
    return Bitmap.createBitmap(
        this,
        0,
        0,
        width,
        height,
        matrix,
        true,
    )
}

fun Bitmap.applyColorFilter(color: Int): Bitmap {
    val result = copy(config, true)
    val paint =
        Paint().apply {
            colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    Canvas(result).drawBitmap(this, 0f, 0f, paint)
    return result
} 
