package com.trainingtimer.timerapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.trainingtimer.R
import com.trainingtimer.timerapp.views.details.TrainingFragment
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {

    private var secRemain: Int = 0
    private var isTimerRunning = false

    private var updateTimer = Timer()
    private var countdownTimer = Timer()

    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("Stopwatch", "Stopwatch onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        getNotificationManager()

        val action = intent?.getStringExtra(STOPWATCH_ACTION)!!

        Log.d("Stopwatch", "onStartCommand Action: $action")

        when (action) {
            START -> startCountdown()
            PAUSE -> pauseCountdown()
            RESET -> resetCountdown()
            GET_STATUS -> sendStatus()
            MOVE_TO_FOREGROUND -> moveToForeground()
            MOVE_TO_BACKGROUND -> moveToBackground()
        }

        return START_STICKY
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Stopwatch",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun sendStatus() {
        val statusIntent = Intent()
        statusIntent.action = STOPWATCH_STATUS
        statusIntent.putExtra(IS_STOPWATCH_RUNNING, isTimerRunning)
        statusIntent.putExtra(TIME_ELAPSED, secRemain)
        sendBroadcast(statusIntent)
    }

    private fun startCountdown() {
        isTimerRunning = true

        sendStatus()

        countdownTimer = Timer()
        countdownTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val countdownIntent = Intent()
                countdownIntent.action = STOPWATCH_TICK

                if (secRemain > 0) {
                    secRemain--
                } else {
                    pauseCountdown()
                }

                countdownIntent.putExtra(TIME_ELAPSED, secRemain)
                sendBroadcast(countdownIntent)
            }
        }, 0, 1000)
    }

    /*
    * This function pauses the stopwatch
    * Sends an update of the current state of the stopwatch
    * */
    private fun pauseCountdown() {
        countdownTimer.cancel()
        isTimerRunning = false
        sendStatus()
    }

    /*
    * This function resets the stopwatch
    * Sends an update of the current state of the stopwatch
    * */
    private fun resetCountdown() {
        pauseCountdown()
        secRemain = 0
        sendStatus()
    }

    /*
    * This function is responsible for building and returning a Notification with the current
    * state of the stopwatch along with the timeElapsed
    * */
    private fun buildNotification(): Notification {
        val title = if (isTimerRunning) {
            "Stopwatch is running!"
        } else {
            "Stopwatch is paused!"
        }

        val hours: Int = secRemain.div(60).div(60)
        val minutes: Int = secRemain.div(60)
        val seconds: Int = secRemain.rem(60)

        val intent = Intent(this, TrainingFragment::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setOngoing(true)
            .setContentText(
                "${"%02d".format(hours)}:${"%02d".format(minutes)}:${
                    "%02d".format(
                        seconds
                    )
                }"
            )
            .setColorized(true)
            .setColor(Color.parseColor("#BEAEE2"))
            .setSmallIcon(R.drawable.ic_clock)
            .setOnlyAlertOnce(true)
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .build()
    }


    /*
    * This function uses the notificationManager to update the existing notification with the new notification
    * */
    private fun updateNotification() {
        notificationManager.notify(
            1,
            buildNotification()
        )
    }

    /*
    * This function is triggered when the app is not visible to the user anymore
    * It check if the stopwatch is running, if it is then it starts a foreground service
    * with the notification.
    * We run another timer to update the notification every second.
    * */
    private fun moveToForeground() {

        if (isTimerRunning) {
            startForeground(1, buildNotification())

            updateTimer = Timer()

            updateTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateNotification()
                }
            }, 0, 1000)
        }
    }

    /*
    * This function is triggered when the app is visible again to the user
    * It cancels the timer which was updating the notification every second
    * It also stops the foreground service and removes the notification
    * */
    private fun moveToBackground() {
        updateTimer.cancel()
        stopForeground(true)
    }

    companion object {
        // Channel ID for notifications
        const val CHANNEL_ID = "Stopwatch_Notifications"

        // Service Actions
        const val START = "START"
        const val PAUSE = "PAUSE"
        const val RESET = "RESET"
        const val GET_STATUS = "GET_STATUS"
        const val MOVE_TO_FOREGROUND = "MOVE_TO_FOREGROUND"
        const val MOVE_TO_BACKGROUND = "MOVE_TO_BACKGROUND"

        // Intent Extras
        const val STOPWATCH_ACTION = "STOPWATCH_ACTION"
        const val TIME_ELAPSED = "TIME_ELAPSED"
        const val IS_STOPWATCH_RUNNING = "IS_STOPWATCH_RUNNING"

        // Intent Actions
        const val STOPWATCH_TICK = "STOPWATCH_TICK"
        const val STOPWATCH_STATUS = "STOPWATCH_STATUS"
    }
}