import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun Bitmap.saveToInternalStorage(
    context: Context,
    filename: String,
): Uri? =
    try {
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { stream ->
            compress(Bitmap.CompressFormat.JPEG, 90, stream)
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file,
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

fun Bitmap.addWatermark(
    watermark: String,
    textSize: Float = 50f,
    color: Int = Color.WHITE,
    alpha: Int = 128,
): Bitmap {
    val result = copy(config, true)
    val canvas = Canvas(result)
    val paint =
        Paint().apply {
            this.textSize = textSize
            this.color = color
            this.alpha = alpha
            isAntiAlias = true
        }

    val bounds = Rect()
    paint.getTextBounds(watermark, 0, watermark.length, bounds)

    val x = (width - bounds.width()) / 2f
    val y = (height + bounds.height()) / 2f

    canvas.drawText(watermark, x, y, paint)
    return result
}

fun Bitmap.cropToCircle(): Bitmap {
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
                BitmapShader(
                    this@cropToCircle,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP,
                )
        }

    val radius = width.coerceAtMost(height) / 2f
    canvas.drawCircle(
        width / 2f,
        height / 2f,
        radius,
        paint,
    )

    return output
} 
