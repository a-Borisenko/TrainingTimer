package com.trainingtimer.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationBroadcastReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DESTROY) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.cancel(1)
        }
    }
}