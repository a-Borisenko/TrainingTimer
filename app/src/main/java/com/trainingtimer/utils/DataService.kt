package com.trainingtimer.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.trainingtimer.domain.Training
import kotlin.properties.Delegates

class DataService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DataService", "started!!!")
        return START_NOT_STICKY /*super.onStartCommand(intent, flags, startId)*/
    }

    override fun onDestroy() {
        Log.d("DataService", "destroyed!!!")
        super.onDestroy()
    }

    companion object {

        var startTime: Long by Delegates.observable(Training.START_TIME) {
                prop, old, new ->
            Log.d("DataService", "startTime = $old -> $new")
        }

        var currentId: Int by Delegates.observable(Training.UNDEFINED_ID) {
                prop, old, new ->
            Log.d("DataService", "currentId = $old -> $new")
        }
            /*if (TimerService.isCounting) {
                TimerService.currentId
                Log.d("DataService", "currentId = ${TimerService.currentId}")
            } else {
                Training.UNDEFINED_ID
                Log.d("DataService", "currentId = ${Training.UNDEFINED_ID}")
            }*/
    }
}