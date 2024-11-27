import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

fun Context.updateLocale(locale: Locale): Context {
    Locale.setDefault(locale)

    val config = Configuration(resources.configuration)
    config.setLocale(locale)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        createConfigurationContext(config)
    } else {
        resources.updateConfiguration(config, resources.displayMetrics)
        this
    }
}

fun Context.isRTL(): Boolean =
    resources.configuration.layoutDirection ==
        Configuration.SCREENLAYOUT_LAYOUTDIR_RTL

fun Context.getCurrentLocale(): Locale =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale
    }

fun String.localizeNumber(locale: Locale = Locale.getDefault()): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val formatter =
            android.icu.text.NumberFormat
                .getInstance(locale)
        formatter.format(this.toLongOrNull() ?: return this)
    } else {
        java.text.NumberFormat.getInstance(locale).format(
            this.toLongOrNull() ?: return this,
        )
    }
}

fun Context.getLocalizedString(
    resourceId: Int,
    locale: Locale,
): String {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration)
        .resources
        .getString(resourceId)
} 
