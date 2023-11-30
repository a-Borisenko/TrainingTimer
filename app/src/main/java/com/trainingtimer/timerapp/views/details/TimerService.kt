package com.trainingtimer.timerapp.views.details

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trainingtimer.R
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {

    private var secRemain: Long = 0
    private var step = 0f
    val timer = Timer()

    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        secRemain = intent.getLongExtra("TimeValue", 0)
        var progr = 100f
        step = progr / (secRemain.toFloat())
//        val timer = Timer()

        createNotificationChannel()
//        getNotificationManager()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (secRemain > 0) {
                    secRemain--
                    progr -= step
                    isCounting = true
                    updateNotification()
                    _secRemainLD.postValue(secRemain)
                    _progressLD.postValue(progr)
                } else {
                    timer.cancel()
                    isCounting = false
                    notificationManager.cancelAll()
                }
            }
        }, 0, 1000)
        return super.onStartCommand(intent, flags, startId)
    }

    /*private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }*/

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown is running!")
            .setOngoing(true)
            .setContentText(TrainingUtils.timeLongToString(secRemain))
            .setSmallIcon(R.drawable.ic_clock)
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pIntent)
//            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    private fun updateNotification() {
        notificationManager.notify(1, buildNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
//        notificationManager.cancel(1)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }*/
        notificationManager.cancelAll()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "NotificationChannelID"
        var isCounting = false

        private val _secRemainLD = MutableLiveData<Long>()
        val secRemainLD: LiveData<Long>
            get() = _secRemainLD

        private val _progressLD = MutableLiveData<Float>()
        val progressLD: LiveData<Float>
            get() = _progressLD
    }
}