package com.trainingtimer.utils

import android.app.Notification
import android.app.NotificationChannel

interface NotificationManager {

    fun createNotificationChannel(channel: NotificationChannel)
    fun showNotification(notification: Notification)
    fun cancelNotification(notificationId: Int)
}
