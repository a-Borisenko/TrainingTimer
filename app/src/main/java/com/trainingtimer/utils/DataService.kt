package com.trainingtimer.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.trainingtimer.domain.Training
import com.trainingtimer.views.details.TimerService

class DataService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {

        var startTime = Training.START_TIME
        var currentId =
            if (TimerService.isCounting) {
                TimerService.currentId
            } else {
                Training.UNDEFINED_ID
            }
    }
}