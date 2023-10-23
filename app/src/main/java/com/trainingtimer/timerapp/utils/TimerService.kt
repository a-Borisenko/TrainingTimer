package com.trainingtimer.timerapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
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

    private lateinit var updateTimer: CountDownTimer
    private lateinit var countdownTimer: CountDownTimer

    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("TimerService", "Countdown onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        getNotificationManager()

        val action = intent?.getStringExtra(COUNTDOWN_ACTION)!!

        Log.d("TimerService", "onStartCommand Action: $action")

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
                "Countdown",
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
        statusIntent.action = COUNTDOWN_STATUS
        statusIntent.putExtra(IS_COUNTDOWN_RUNNING, isTimerRunning)
        statusIntent.putExtra(TIME_ELAPSED, secRemain)
        sendBroadcast(statusIntent)
    }

    private fun startCountdown() {
        isTimerRunning = true

        sendStatus()

        countdownTimer = object : CountDownTimer(TrainingFragment.secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                Log.d("TimerService", "done!!!")
            }

            override fun onTick(millisUntilFinished: Long) {
                val countdownIntent = Intent()
                countdownIntent.action = COUNTDOWN_TICK

                TrainingFragment.secondsRemaining = millisUntilFinished / 1000

                countdownIntent.putExtra(TIME_ELAPSED, secRemain)
                sendBroadcast(countdownIntent)
            }
        }.start()
        /*countdownTimer = Timer()
        countdownTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val countdownIntent = Intent()
                countdownIntent.action = COUNTDOWN_TICK

                if (secRemain > 0) {
                    secRemain--
                } else {
                    pauseCountdown()
                }

                countdownIntent.putExtra(TIME_ELAPSED, secRemain)
                sendBroadcast(countdownIntent)
            }
        }, 0, 1000)*/
    }

    private fun pauseCountdown() {
        countdownTimer.cancel()
        isTimerRunning = false
        sendStatus()
    }

    private fun resetCountdown() {
        pauseCountdown()
        secRemain = 0
        sendStatus()
    }

    private fun buildNotification(): Notification {
        val title = if (isTimerRunning) {
            "Countdown is running!"
        } else {
            "Countdown is paused!"
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


    private fun updateNotification() {
        notificationManager.notify(
            1,
            buildNotification()
        )
    }

    private fun moveToForeground() {

        if (isTimerRunning) {
            startForeground(1, buildNotification())

            updateTimer = object : CountDownTimer(TrainingFragment.secondsRemaining * 1000, 1000) {
                override fun onFinish() {
                    Log.d("TimerService", "done!!!")
                }

                override fun onTick(millisUntilFinished: Long) {
                    TrainingFragment.secondsRemaining = millisUntilFinished / 1000
                    updateNotification()
                }
            }.start()
            /*updateTimer = Timer()

            updateTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateNotification()
                }
            }, 0, 1000)*/
        }
    }

    private fun moveToBackground() {
        updateTimer.cancel()
        stopForeground(true)
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