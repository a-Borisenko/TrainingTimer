package com.trainingtimer

import android.app.Application
import com.trainingtimer.data.TrainingRepository

class TrainingTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        TrainingRepository.initialize(this)
    }
}