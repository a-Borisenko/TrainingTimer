package com.trainingtimer.views.details

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.trainingtimer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@DisableInstallInCheck
@Module
class TimerService @Inject constructor() : Service() {

    private var secRemain = 0L
    private var startTime = 0L
//    private var step = 0f
    private var progress = 100f

    private lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("TimerService", "Service started")
        createNotificationChannel()
        startForeground(1, buildNotification())
        startTime = intent.getLongExtra(TIME_VALUE, 0)
        val action = intent.getStringExtra(CURRENT_STATE)
        secRemain = startTime
        secInit = startTime

        when (action){
            READY -> readyToCountdown()
            START -> startCountdown()
            FINISHED -> finishedCountdown()
        }

        return START_STICKY
    }

    fun readyToCountdown() {
        Log.d("service State", "READY")
        _progressFlow.value = 100f
    }

    fun zeroCountdown() {
        progress = 0f
        _progressFlow.value = 0f
    }

    fun finishedCountdown() {
        Log.d("service State", "FINISHED")
        isCounting = false
        zeroCountdown()
        notificationManager.cancelAll()
        onDestroy()
    }

    fun startCountdown() {
        Log.d("service State", "START")
        progress = 100f
//        step = progress / (secRemain.toFloat())
        _secRemainFlow.onStart {
            while (secRemain > 0L) {
                delay(1000)
                _secRemainFlow.value = --secRemain
                progress = (secRemain.toFloat() / startTime.toFloat()) * 100f
//                progress -= step
                isCounting = true
                updateNotification()
                _progressFlow.value = progress
                Log.d("TimerService", "sec = $secRemain; progr = $progress")
                emit(secRemain)
            }
            finishedCountdown()
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    /*fun cancelCountdown() {
        Log.d("Time Service", "cancel countdown")
        secRemain = 0L
        secInit = startTime
        secRemain = startTime
        readyToCountdown()
    }*/

    @Provides
    fun timeFlow() = flow {
        while (secRemain > 0L) {
            delay(1000)
            Log.d("timeFlow", "emit $secRemain")    //don't work
            emit(secRemain)
        }
    }.flowOn(Dispatchers.IO)

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown is running!")
//            .setOngoing(true)
            .setContentText(timeLongToString(secRemain))
            .setSmallIcon(R.drawable.ic_clock)
            .setChannelId(CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
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
        Log.d("TimerService", "Service Stopped")
        stopForeground(true)
        super.onDestroy()
    }

    companion object {
        var isCounting = false
        var secInit = 0L
        var progressInit = 0f

        private val _secRemainFlow = MutableStateFlow(secInit)
        val secRemainFlow: StateFlow<Long> = _secRemainFlow.asStateFlow()

        private val _progressFlow = MutableStateFlow(progressInit)
        val progressFlow: StateFlow<Float> = _progressFlow.asStateFlow()

        /*private val _secRemainLD = MutableLiveData<Long>()
        val secRemainLD: LiveData<Long>
            get() = _secRemainLD*/
    }
}