import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun SharedPreferences.string(
    key: String,
    defaultValue: String = ""
): ReadWriteProperty<Any, String> = object : ReadWriteProperty<Any, String> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return getString(key, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        edit().putString(key, value).apply()
    }
}

fun SharedPreferences.int(
    key: String,
    defaultValue: Int = 0
): ReadWriteProperty<Any, Int> = object : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return getInt(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        edit().putInt(key, value).apply()
    }
}

fun SharedPreferences.boolean(
    key: String,
    defaultValue: Boolean = false
): ReadWriteProperty<Any, Boolean> = object : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return getBoolean(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        edit().putBoolean(key, value).apply()
    }
}

suspend fun Context.clearApplicationData() {
    withContext(Dispatchers.IO) {
        val directory = cacheDir.parentFile
        if (directory != null && directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.name != "lib") {
                    file.deleteRecursively()
                }
            }
        }

        // Clear SharedPreferences
        val sharedPrefsDir = File(applicationInfo.dataDir, "shared_prefs")
        if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
            sharedPrefsDir.listFiles()?.forEach { it.delete() }
        }
    }
}

suspend fun Context.calculateDirectorySize(directory: File): Long {
    return withContext(Dispatchers.IO) {
        directory.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }
} 