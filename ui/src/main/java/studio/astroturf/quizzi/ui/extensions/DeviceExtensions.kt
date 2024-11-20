import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.getSystemService

fun Context.getDeviceId(): String {
    return Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

fun Context.getScreenMetrics(): DisplayMetrics {
    val windowManager = getSystemService<WindowManager>()
    val displayMetrics = DisplayMetrics()
    windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    return displayMetrics
}

fun Context.isTablet(): Boolean {
    return resources.configuration.screenLayout and
            android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK >=
            android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
}

fun Context.getDeviceInfo(): DeviceInfo {
    return DeviceInfo(
        manufacturer = Build.MANUFACTURER,
        model = Build.MODEL,
        sdkVersion = Build.VERSION.SDK_INT,
        versionCode = try {
            packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        },
        versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    )
}

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val sdkVersion: Int,
    val versionCode: Int,
    val versionName: String
)

fun Context.getScreenDensity(): String {
    return when (resources.displayMetrics.densityDpi) {
        DisplayMetrics.DENSITY_LOW -> "LDPI"
        DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
        DisplayMetrics.DENSITY_HIGH -> "HDPI"
        DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
        DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
        DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
        else -> "Unknown"
    }
}

fun Context.isEmulator(): Boolean {
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT.contains("sdk_gphone")
            || Build.PRODUCT.contains("google_sdk")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("sdk_x86")
            || Build.PRODUCT.contains("vbox86p")
            || Build.PRODUCT.contains("emulator")
            || Build.PRODUCT.contains("simulator")
} 