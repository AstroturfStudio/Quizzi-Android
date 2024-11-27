import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

fun Int.formatWithSeparator(separator: Char = '.'): String = String.format(Locale.getDefault(), "%,d", this).replace(',', separator)

fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}

fun Float.roundTo(decimals: Int): Float {
    val factor = 10f.pow(decimals)
    return (this * factor).roundToInt() / factor
}

fun Long.formatFileSize(): String =
    when {
        this < 1024 -> "$this B"
        this < 1024 * 1024 -> "${(this / 1024f).roundTo(1)} KB"
        this < 1024 * 1024 * 1024 -> "${(this / (1024f * 1024f)).roundTo(1)} MB"
        else -> "${(this / (1024f * 1024f * 1024f)).roundTo(1)} GB"
    }

fun Int.toCompactFormat(): String =
    when {
        this < 1000 -> toString()
        this < 1_000_000 -> "${(this / 1000f).roundTo(1)}K"
        this < 1_000_000_000 -> "${(this / 1_000_000f).roundTo(1)}M"
        else -> "${(this / 1_000_000_000f).roundTo(1)}B"
    }

fun Double.toCurrency(
    locale: Locale = Locale.getDefault(),
    currency: Currency = Currency.getInstance(locale),
): String =
    NumberFormat
        .getCurrencyInstance(locale)
        .apply {
            this.currency = currency
        }.format(this)

fun Int.toRomanNumeral(): String {
    val romanNumerals =
        listOf(
            1000 to "M",
            900 to "CM",
            500 to "D",
            400 to "CD",
            100 to "C",
            90 to "XC",
            50 to "L",
            40 to "XL",
            10 to "X",
            9 to "IX",
            5 to "V",
            4 to "IV",
            1 to "I",
        )

    var number = this
    return buildString {
        for ((value, numeral) in romanNumerals) {
            while (number >= value) {
                append(numeral)
                number -= value
            }
        }
    }
}

fun Int.toOrdinal(): String {
    val suffixes = listOf("th", "st", "nd", "rd")
    return when {
        this % 100 in 11..13 -> "${this}th"
        else -> "${this}${suffixes.getOrNull(this % 10) ?: "th"}"
    }
}

fun Double.toPercentage(decimals: Int = 0): String = "%.${decimals}f%%".format(this * 100)

fun Int.isEven(): Boolean = this % 2 == 0

fun Int.isOdd(): Boolean = !isEven()

fun Int.factorial(): Long {
    require(this >= 0) { "Factorial is not defined for negative numbers" }
    var result = 1L
    for (i in 2..this) {
        result *= i
    }
    return result
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()

fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()

fun Int.toTimeString(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        if (seconds > 0 || (hours == 0 && minutes == 0)) append("${seconds}s")
    }.trim()
} 
