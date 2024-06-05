package com.trainingtimer.utils

/*import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManagerImpl @Inject constructor(
    context: Context
) : NotificationManager {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    override fun createNotificationChannel(channel: NotificationChannel) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showNotification(notification: Notification) {
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "NotificationChannelID"
    }
}*/
