package com.trainingtimer.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trainingtimer.utils.NotificationManagerImpl.Companion.NOTIFICATION_ID
import javax.inject.Inject

class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_DISMISS) {
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }
}
