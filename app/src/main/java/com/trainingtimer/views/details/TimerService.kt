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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trainingtimer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@DisableInstallInCheck
@Module
class TimerService @Inject constructor() : Service() {

    private var secRemain: Long = 0
    private var step = 0f

    private val _secRemainFlow = MutableSharedFlow<Long>(replay = 0)
    val secRemainFlow: SharedFlow<Long> = _secRemainFlow

//    lateinit var secRemainFlow: Flow<Long>
    /*val secRemainFlow: Flow<Long> = flow {
        for (time in secRemain..0) {
            delay(1000)
            emit(time)
            Log.d("secRemainFlow", "emit ${emit(time)}")
        }
    }*/
    /*private val secRemainFlow = MutableSharedFlow<Long>(
        replay = 0, //do not send events to new subscribers which have been emitted before subscription
        extraBufferCapacity = 1, //min. buffer capacity for using DROP_OLDEST overflow policy
        onBufferOverflow = BufferOverflow.DROP_OLDEST //newest item will replace oldest item in case of buffer overflow
    )*/

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
//        timeFlow()
        _secRemainFlow.onStart {
            for (time in (secRemain - 1) downTo 0L) {
                emit(time)
                Log.d("secRemainFlow", "emit $time")
                delay(1000)
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        object : CountDownTimer(secRemain * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secRemain--
                progress -= step
                isCounting = true
                updateNotification()
//                _secRemainLD.postValue(secRemain)
                _progressLD.postValue(progress)
                Log.d("onTick", "secRemain = $secRemain")
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
    fun timeFlow() = flow {
        for (time in (secRemain - 1) downTo 0L) {
            delay(1000)
            emit(time)
            Log.d("timeFlow", "emit $time")
        }
    }.launchIn(CoroutineScope(Dispatchers.Default))

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