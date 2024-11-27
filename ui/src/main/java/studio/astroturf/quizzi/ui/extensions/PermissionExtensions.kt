import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission,
    ) == PackageManager.PERMISSION_GRANTED

fun Context.hasPermissions(vararg permissions: String): Boolean = permissions.all { hasPermission(it) }

fun ComponentActivity.requestPermissionWithFlow(permission: String) =
    callbackFlow {
        val launcher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { isGranted ->
                trySend(isGranted)
            }

        launcher.launch(permission)

        awaitClose {
            // Cleanup if needed
        }
    }

fun ComponentActivity.requestMultiplePermissionsWithFlow(permissions: Array<String>) =
    callbackFlow {
        val launcher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
            ) { results ->
                trySend(results)
            }

        launcher.launch(permissions)

        awaitClose {
            // Cleanup if needed
        }
    }

object PermissionUtils {
    val LOCATION_PERMISSIONS =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }

    val CAMERA_PERMISSIONS =
        arrayOf(
            Manifest.permission.CAMERA,
        )

    val STORAGE_PERMISSIONS =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
}

sealed class PermissionStatus {
    object Granted : PermissionStatus()

    object Denied : PermissionStatus()

    object PermanentlyDenied : PermissionStatus()

    fun isGranted() = this is Granted
}

fun ComponentActivity.checkPermissionStatus(permission: String): PermissionStatus =
    when {
        hasPermission(permission) -> PermissionStatus.Granted
        shouldShowRequestPermissionRationale(permission) -> PermissionStatus.Denied
        else -> PermissionStatus.PermanentlyDenied
    }
