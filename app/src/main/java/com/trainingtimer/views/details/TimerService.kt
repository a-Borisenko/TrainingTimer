package com.trainingtimer.views.details

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.trainingtimer.MainActivity
import com.trainingtimer.R
import com.trainingtimer.utils.DataService
import com.trainingtimer.utils.DataService.Companion.START
import com.trainingtimer.utils.timeLongToString
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    private var secRemain = 0L
    private var startTime: Long by Delegates.observable(DataService.startTime) { _, _, _ ->
        secRemain = startTime
    }

    private lateinit var notificationManager: NotificationManager
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (!isCounting) {
            createNotificationChannel()
            startForeground(1, createNotification())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return when (intent.action) {
            START -> {
                startCountdown()
                START_STICKY
            }
            DESTROY -> {
                stopCountdown()
                START_NOT_STICKY
            }
            else -> START_STICKY
        }
    }

    private fun startCountdown() {
        startTime = DataService.startTime
        coroutineScope.launch {
            isCounting = true
            while (secRemain > 0L) {
                delay(1000)
                _secRemainFlow.value = --secRemain
                val progress = (secRemain.toFloat() * 100f) / startTime.toFloat()
                _progressFlow.value = progress

                updateNotification()
                Log.d("TimerService", "sec = $secRemain; progress = $progress")
            }
            isCounting = false
            stopSelf()
        }
    }

    private fun stopCountdown() {
        secRemain = 1L
        _secRemainFlow.value = secRemain
        stopSelf()
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown is running!")
            .setContentText(timeLongToString(secRemain))
            .setSmallIcon(R.drawable.ic_clock)
            .setContentIntent(openAppIntent)
            .addAction(R.drawable.cancel_button_black, "Dismiss", stopServiceIntent)
            .build()
    }

    private fun updateNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown is running!")
            .setContentText(timeLongToString(secRemain))
            .setSmallIcon(R.drawable.ic_clock)
            .setContentIntent(openAppIntent)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Countdown Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroy() {
        Log.d("TimerService", "Service Stopped")
        super.onDestroy()
    }

    private val openAppIntent by lazy {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val stopServiceIntent by lazy {
        val stopIntent = Intent(appContext, TimerService::class.java).apply {
            action = DESTROY
        }
        PendingIntent.getService(
            appContext,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        var isCounting: Boolean by Delegates.observable(DataService.isCounting) { _, _, new ->
            DataService.isCounting = new
        }

        var isLast = true
        private const val DESTROY = "DESTROY"
        private const val CHANNEL_ID = "NotificationChannelID"

        private val _secRemainFlow = MutableStateFlow(DataService.startTime)
        val secRemainFlow: StateFlow<Long> = _secRemainFlow.asStateFlow()

        private val _progressFlow = MutableStateFlow(100f)
        val progressFlow: StateFlow<Float> = _progressFlow.asStateFlow()

        fun newIntent(context: Context, action: String): Intent {
            return Intent(context, TimerService::class.java).apply {
                this.action = action
            }
        }
    }
}