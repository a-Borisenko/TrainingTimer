package com.trainingtimer.views.details

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
import com.trainingtimer.domain.Training
import com.trainingtimer.utils.DataService
import com.trainingtimer.utils.DataService.Companion.START
import com.trainingtimer.utils.timeLongToString
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    private var secRemain = 0L
    private var progress = 100f

    private var startTime: Long by Delegates.observable(DataService.startTime) { _, _, _ ->
        secRemain = startTime
    }

    private lateinit var notificationManager: NotificationManager
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onBind(p0: Intent): IBinder? {
        TODO("Not yet implemented")
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
                secRemain = 1L
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
                progress = (secRemain.toFloat() * 100f) / startTime.toFloat()
                updateNotification()
                _progressFlow.value = progress
                Log.d("TimerService", "sec = $secRemain; progress = $progress")
                emitTwoValues(secRemain, progress)
            }
            isCounting = false
            stopSelf()
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Countdown is running!")
        .setContentText(timeLongToString(secRemain))
        .setSmallIcon(R.drawable.ic_clock)
        .setContentIntent(openAppIntent)
        .addAction(R.drawable.cancel_button_black, "Dismiss", stopServiceIntent)
        .build()

    private fun updateNotification() {
        notificationManager.notify(1, createNotification())
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "channel_name",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun emitTwoValues(longValue: Long, floatValue: Float): Flow<Pair<Long, Float>> {
        return flow {
            emit(Pair(longValue, floatValue))
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
        val stopIntent = Intent(appContext, TimerService::class.java)
        stopIntent.action = DESTROY
        PendingIntent.getService(
            appContext,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        var isCounting: Boolean by Delegates.observable(DataService.isCounting) { prop, old, new ->
            DataService.isCounting = new
        }

        private const val DESTROY = "DESTROY"
        private const val CHANNEL_ID = "NotificationChannelID"

        var isLast = true
        var secInit = 0L
        var progressInit = 0f
        var currentId = Training.UNDEFINED_ID

        private val _secRemainFlow = MutableStateFlow(secInit)
        val secRemainFlow: StateFlow<Long> = _secRemainFlow.asStateFlow()

        private val _progressFlow = MutableStateFlow(progressInit)
        val progressFlow: StateFlow<Float> = _progressFlow.asStateFlow()

        /*private val _secRemainLD = MutableLiveData<Long>()
        val secRemainLD: LiveData<Long>
            get() = _secRemainLD*/

        fun newIntent(context: Context, action: String): Intent {
            return Intent(context, TimerService::class.java).apply {
                this.action = action
            }
        }
    }
}