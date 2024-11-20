import android.util.Base64
import java.security.MessageDigest

fun String.hash(algorithm: String = "SHA-256"): String {
    val digest = MessageDigest.getInstance(algorithm)
    val hash = digest.digest(toByteArray())
    return hash.fold("") { str, byte -> str + "%02x".format(byte) }
}

fun String.toBase64(): String =
    Base64.encodeToString(toByteArray(), Base64.DEFAULT)

fun String.fromBase64(): String =
    String(Base64.decode(this, Base64.DEFAULT))