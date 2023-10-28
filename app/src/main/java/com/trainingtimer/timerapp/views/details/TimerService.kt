package com.trainingtimer.timerapp.views.details

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.trainingtimer.R
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {

    private var secRemain: Int = 0
//    private var isTimerRunning = false

//    private lateinit var updateTimer: CountDownTimer
//    private lateinit var countdownTimer: CountDownTimer

    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        secRemain = intent.getIntExtra("TimeValue", 0)
        val timer = Timer()
        getNotificationManager()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val intentLocal = Intent()
                intentLocal.action = "Counter"
                if (secRemain > 0) {
                    secRemain--
                    updateNotification()
                } else {
                    timer.cancel()
                    notificationManager.cancelAll()
                }
                intentLocal.putExtra("TimeRemaining", secRemain)
                sendBroadcast(intentLocal)
            }
        }, 0, 1000)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun buildNotification(): Notification {
        val title = "Countdown is running!"

        val minutes: Int = secRemain.div(60)
        val seconds: Int = secRemain.rem(60)

        val intent = Intent(this, TimerFragment::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setOngoing(true)
            .setContentText("${"%02d".format(minutes)}:${"%02d".format(seconds)}")
            .setColorized(true)
            .setColor(Color.parseColor("#BEAEE2"))
            .setSmallIcon(R.drawable.ic_clock)
            .setOnlyAlertOnce(true)
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .build()
    }


    private fun updateNotification() {
        notificationManager.notify(1, buildNotification())
    }

    companion object {
        // Channel ID for notifications
        const val CHANNEL_ID = "Countdown_Notifications"

        // Service Actions
        const val START = "START"
        const val PAUSE = "PAUSE"
        const val RESET = "RESET"
        const val GET_STATUS = "GET_STATUS"
        const val MOVE_TO_FOREGROUND = "MOVE_TO_FOREGROUND"
        const val MOVE_TO_BACKGROUND = "MOVE_TO_BACKGROUND"

        // Intent Extras
        const val COUNTDOWN_ACTION = "COUNTDOWN_ACTION"
        const val TIME_ELAPSED = "TIME_ELAPSED"
        const val IS_COUNTDOWN_RUNNING = "IS_COUNTDOWN_RUNNING"

        // Intent Actions
        const val COUNTDOWN_TICK = "COUNTDOWN_TICK"
        const val COUNTDOWN_STATUS = "COUNTDOWN_STATUS"
    }
}