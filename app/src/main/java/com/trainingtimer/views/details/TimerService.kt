package com.trainingtimer.views.details

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trainingtimer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@DisableInstallInCheck
@Module
class TimerService @Inject constructor() : Service() {

    private var secRemain: Long = 0
    private var step = 0f
    private val secRemainFlow = MutableSharedFlow<Long>(
        replay = 0, //do not send events to new subscribers which have been emitted before subscription
        extraBufferCapacity = 1, //min. buffer capacity for using DROP_OLDEST overflow policy
        onBufferOverflow = BufferOverflow.DROP_OLDEST //newest item will replace oldest item in case of buffer overflow
    )

    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(1, buildNotification())
        secRemain = intent.getLongExtra("TimeValue", 0)
        var progress = 100f
        step = progress / (secRemain.toFloat())

        object : CountDownTimer(secRemain * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secRemain--
                progress -= step
                isCounting = true
                updateNotification()
//                _secRemainLD.postValue(secRemain)
                _progressLD.postValue(progress)
            }
            override fun onFinish() {
                isCounting = false
                notificationManager.cancelAll()
                onDestroy()
            }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    @Provides
    fun listenCurrentTime(): Flow<Long> = secRemainFlow

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown is running!")
            .setOngoing(true)
            .setContentText(timeLongToString(secRemain))
            .setSmallIcon(R.drawable.ic_clock)
            .setChannelId(CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun updateNotification() {
        notificationManager.notify(1, buildNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopForeground(true)
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "NotificationChannelID"
        var isCounting = false

        /*private val _secRemainLD = MutableLiveData<Long>()
        val secRemainLD: LiveData<Long>
            get() = _secRemainLD*/

        private val _progressLD = MutableLiveData<Float>()
        val progressLD: LiveData<Float>
            get() = _progressLD
    }
}