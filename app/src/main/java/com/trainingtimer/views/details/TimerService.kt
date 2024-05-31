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
import com.trainingtimer.utils.CHANNEL_ID
import com.trainingtimer.utils.DataService
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

    /*private val dismissIntent by lazy {
        val intent = Intent(this, TimerService::class.java).apply {
            notificationManager.cancelAll()
            stopSelf()
        }
        PendingIntent.getForegroundService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }*/

    /*private val notificationIntent = Intent(applicationContext, MainActivity::class.java)
    private val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        notificationIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )*/

    private var secRemain = 0L
    private var progress = 100f

    private var startTime: Long by Delegates.observable(DataService.startTime) { _, _, _ ->
        secRemain = startTime
    }

    private lateinit var notificationManager: NotificationManager
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onBind(p0: Intent): IBinder? {
        TODO("Not yet implemented")
//        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (!isCounting) {
            createNotificationChannel()
            startForeground(1, createNotification())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!isCounting) {
            startCountdown()
        }

        /*currentId = intent.getIntExtra("id", Training.UNDEFINED_ID)
//        createNotificationChannel()
//        startForeground(1, buildNotification())
        startTime = intent.getLongExtra(TIME_VALUE, 0L)
        val action = intent.getStringExtra(CURRENT_STATE)
//        secRemain = startTime
//        secInit = startTime
        Log.d("TimerService", "Service started, ID = $currentId")

        when (action) {
//            READY -> readyToCountdown()
//            START -> startCountdown(startTime)
//            FINISHED -> finishedCountdown()
            DESTROY -> cancelCountdown()
        }
        if (currentId == Training.UNDEFINED_ID) {
            zeroCountdown()
        } else {
            readyToCountdown()
        }*/

        return START_STICKY
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
                Log.d("service timer", "sec = $secRemain; progr = $progress")
                emitTwoValues(secRemain, progress)
            }
            isCounting = false
            stopSelf()
//            finishedCountdown()
        }
    }

    /*fun readyToCountdown() {
        Log.d("service State", "READY")
        progress = 100f
        _progressFlow.value = 100f
    }*/

    /*fun zeroCountdown() {
        progress = 0f
        _progressFlow.value = 0f
    }*/

    /*fun finishedCountdown() {
        Log.d("service State", "FINISHED")
        isCounting = false
        zeroCountdown()
        if (isLast) {
            notificationManager.cancelAll()
            onDestroy()
        }
    }*/

    /*fun startCountdown(time: Long) {
        Log.d("service State", "START")
        startTime = time
        secRemain = time
//        isLast = false
        createNotificationChannel()
        startForeground(1, buildNotification())

        _secRemainFlow.onStart {
            while (secRemain > 0L) {
                delay(1000)
                _secRemainFlow.value = --secRemain
                progress = (secRemain.toFloat() * 100f) / startTime.toFloat()
                isCounting = true
                updateNotification()
                _progressFlow.value = progress
                Log.d("service timer", "sec = $secRemain; progr = $progress")
                emit(secRemain)
            }
            finishedCountdown()
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }*/

    /*fun cancelCountdown() {
        Log.d("service timer", "cancel countdown")
        this.stopForeground(false)
        finishedCountdown()
    }*/

    /*@Provides
    fun timeFlow() = flow {
        while (secRemain > 0L) {
            delay(1000)
            Log.d("timeFlow", "emit $secRemain")    //don't work
            emit(secRemain)
        }
    }.flowOn(Dispatchers.IO)*/

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Countdown is running!")
        .setContentText(timeLongToString(secRemain))
        .setSmallIcon(R.drawable.ic_clock)
        .setContentIntent(openAppIntent)
//        .addAction(R.drawable.cancel_button_black, "Dismiss", dismissIntent)
        .build()

    /*private fun buildNotification(): Notification {

        val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle("My Notification")
            .setContentText("This is a notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.cancel_button_black, "Dismiss", dismissIntent)
            .addAction(R.drawable.check, "Open App", openAppIntent)

        return builder.build()

//        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            applicationContext,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//
//        val deleteIntent = Intent(applicationContext, TimerService::class.java)
//        deleteIntent.putExtra(CURRENT_STATE, DESTROY)
//        val deletePendingIntent = PendingIntent.getService(
//            applicationContext,
//            0,
//            deleteIntent,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//
//        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
//            .setContentTitle("Countdown is running!")
//            .setContentText(timeLongToString(secRemain))
//            .setSmallIcon(R.drawable.ic_clock)
//            .setChannelId(CHANNEL_ID)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setDeleteIntent(deletePendingIntent)
//            .addAction(R.drawable.cancel_button_black, "Cancel", deletePendingIntent)
//            .build()
    }*/

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
//        stopForeground(true)
        super.onDestroy()
    }

    companion object {
        var isCounting: Boolean by Delegates.observable(DataService.isCounting) { prop, old, new ->
            DataService.isCounting = new
        }

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

        fun newIntent(context: Context): Intent {
            return Intent(context, TimerService::class.java)
        }
    }
}