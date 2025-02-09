import java.security.MessageDigest

fun String.sha256(): String =
    MessageDigest
        .getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }

fun String.truncate(
    maxLength: Int,
    ellipsis: String = "...",
): String =
    if (length <= maxLength) {
        this
    } else {
        take(maxLength - ellipsis.length) + ellipsis
    }

fun String.isNumeric(): Boolean = matches(Regex("^[0-9]+$"))

fun String?.isNotNullOrBlank(): Boolean = !isNullOrBlank()
