package com.trainingtimer

import android.app.Application

class TrainingTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        TrainingRepository.initialize(this)
    }
}