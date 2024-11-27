import android.util.Patterns
import java.util.regex.Pattern

object ValidationPatterns {
    val USERNAME = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$")
    val PASSWORD =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    val PHONE = Pattern.compile("^\\+?[1-9]\\d{1,14}$")
    val URL =
        Pattern.compile(
            "^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$",
        )
}

sealed class ValidationResult {
    object Success : ValidationResult()

    data class Error(
        val message: String,
    ) : ValidationResult()
}

fun String.isValidUsername(): Boolean = ValidationPatterns.USERNAME.matcher(this).matches()

fun String.isValidPassword(): ValidationResult =
    when {
        length < 8 -> ValidationResult.Error("Password must be at least 8 characters")
        !contains(Regex("[A-Z]")) -> ValidationResult.Error("Must contain uppercase letter")
        !contains(Regex("[a-z]")) -> ValidationResult.Error("Must contain lowercase letter")
        !contains(Regex("[0-9]")) -> ValidationResult.Error("Must contain number")
        !contains(Regex("[@#\$%^&+=]")) -> ValidationResult.Error("Must contain special character")
        else -> ValidationResult.Success
    }

fun String.isValidPhoneNumber(): Boolean = ValidationPatterns.PHONE.matcher(this).matches()

fun String.isValidUrl(): Boolean = ValidationPatterns.URL.matcher(this).matches()

fun String.isValidEmail(): ValidationResult =
    when {
        isEmpty() -> ValidationResult.Error("Email cannot be empty")
        !Patterns.EMAIL_ADDRESS.matcher(this).matches() ->
            ValidationResult.Error("Invalid email format")

        else -> ValidationResult.Success
    }

fun String.containsSpecialCharacters(): Boolean = matches(Regex(".*[@#\$%^&+=].*"))

fun String.isValidIpAddress(): Boolean =
    matches(Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) 
