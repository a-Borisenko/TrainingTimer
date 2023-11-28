package com.trainingtimer.timerapp.views.details

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trainingtimer.R
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {

    private var secRemain: Long = 0
    private var step = 0f

    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        secRemain  = intent.getLongExtra("TimeValue", 0)
        var progr = 100f
        step = progr / (secRemain.toFloat())
        val timer = Timer()

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle("Countdown is running!")
            .setContentText(TrainingUtils.timeLongToString(secRemain))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        createNotificationChannel()
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }

//        getNotificationManager()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (secRemain > 0) {
                    secRemain--
                    progr -= step
                    isCounting = true
//                    updateNotification()
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
        Log.d("TimerService", "getNotificationManager")
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }*/

    private fun buildNotification(): Notification {
        Log.d("TimerService", "buildNotification")
        /*val title = "Countdown is running!"

        val minutes = secRemain.div(60)
        val seconds = secRemain.rem(60)

        val intent = Intent(this, TrainingFragment::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )*/

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown is running!")
            .setOngoing(true)
            .setContentText(TrainingUtils.timeLongToString(secRemain))
//            .setContentText("${"%02d".format(minutes)}:${"%02d".format(seconds)}")
//            .setColorized(true)
//            .setColor(Color.parseColor("#BEAEE2"))
            .setSmallIcon(R.drawable.ic_clock)
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    /*private fun updateNotification() {
        Log.d("TimerService", "updateNotification")
        notificationManager.notify(1, buildNotification())
    }*/

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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