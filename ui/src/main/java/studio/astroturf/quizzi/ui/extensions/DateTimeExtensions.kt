import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun Long.toFormattedDate(pattern: String = "dd/MM/yyyy"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}

fun Long.toRelativeTimeString(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    return when {
        diff < 60_000 -> "Az önce"
        diff < 3600_000 -> "${diff / 60_000} dakika önce"
        diff < 86400_000 -> "${diff / 3600_000} saat önce"
        diff < 604800_000 -> "${diff / 86400_000} gün önce"
        else -> toFormattedDate()
    }
}

fun Duration.toReadableString(): String {
    val seconds = inWholeSeconds
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
        else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
    }
}

fun Long.formatCountdown(): String {
    val duration = this.milliseconds
    val minutes = duration.inWholeMinutes
    val seconds = duration.inWholeSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

fun LocalDateTime.toEpochMilli(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun LocalDate.isToday(): Boolean {
    return this == LocalDate.now()
}

fun LocalDate.isFuture(): Boolean {
    return this.isAfter(LocalDate.now())
}

fun LocalDate.isPast(): Boolean {
    return this.isBefore(LocalDate.now())
}

fun LocalDateTime.formatForDisplay(pattern: String = "dd MMM yyyy, HH:mm"): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalDateTime.isSameDay(other: LocalDateTime): Boolean {
    return this.toLocalDate() == other.toLocalDate()
} 