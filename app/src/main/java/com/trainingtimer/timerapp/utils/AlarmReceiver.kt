package com.trainingtimer.timerapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val message = intent.getStringExtra("key1")
        Log.d("AlarmReceiver", "Alarm Date 1: $message")
        val mes = intent.getStringExtra("key2")
        Log.d("AlarmReceiver", "Alarm Date 2: $mes")
    }
}