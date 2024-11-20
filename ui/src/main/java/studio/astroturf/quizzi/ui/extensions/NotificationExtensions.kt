import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat

data class NotificationConfig(
    val channelId: String,
    val channelName: String,
    val channelDescription: String,
    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    val showBadge: Boolean = true
)

fun Context.createNotificationChannel(config: NotificationConfig) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            config.channelId,
            config.channelName,
            config.importance
        ).apply {
            description = config.channelDescription
            setShowBadge(config.showBadge)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

@RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
fun Context.showSimpleNotification(
    channelId: String,
    title: String,
    content: String,
    notificationId: Int,
    intent: Intent? = null,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT
) {
    val pendingIntent = intent?.let {
        PendingIntent.getActivity(
            this,
            0,
            it,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(priority)
        .setAutoCancel(true)
        .apply {
            pendingIntent?.let { setContentIntent(it) }
        }

    NotificationManagerCompat.from(this).notify(notificationId, builder.build())
}

@RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
fun Context.showBigTextNotification(
    channelId: String,
    title: String,
    content: String,
    bigText: String,
    notificationId: Int
) {
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(content)
        .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    NotificationManagerCompat.from(this).notify(notificationId, builder.build())
}

@RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
fun Context.showProgressNotification(
    channelId: String,
    title: String,
    content: String,
    progress: Int,
    max: Int,
    notificationId: Int,
    indeterminate: Boolean = false
) {
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .setProgress(max, progress, indeterminate)

    NotificationManagerCompat.from(this).notify(notificationId, builder.build())
}

@RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
fun Context.showImageNotification(
    channelId: String,
    title: String,
    content: String,
    largeIcon: Bitmap,
    bigPicture: Bitmap,
    notificationId: Int
) {
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(content)
        .setLargeIcon(largeIcon)
        .setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(bigPicture)
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    NotificationManagerCompat.from(this).notify(notificationId, builder.build())
}

@RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
fun Context.showActionNotification(
    channelId: String,
    title: String,
    content: String,
    notificationId: Int,
    vararg actions: NotificationAction
) {
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    actions.forEach { action ->
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            action.requestCode,
            action.intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.addAction(
            NotificationCompat.Action.Builder(
                action.icon,
                action.title,
                pendingIntent
            ).build()
        )
    }

    NotificationManagerCompat.from(this).notify(notificationId, builder.build())
}

data class NotificationAction(
    val icon: IconCompat,
    val title: String,
    val intent: Intent,
    val requestCode: Int
)

fun Context.cancelNotification(notificationId: Int) {
    NotificationManagerCompat.from(this).cancel(notificationId)
}

fun Context.cancelAllNotifications() {
    NotificationManagerCompat.from(this).cancelAll()
} 