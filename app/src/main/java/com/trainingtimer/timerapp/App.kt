package com.trainingtimer.timerapp

import android.app.Application
import com.trainingtimer.foundation.data.TrainingRepositoryImpl

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TrainingRepositoryImpl.initialize(this)
    }
}