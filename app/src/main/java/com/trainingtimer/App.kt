package com.trainingtimer

import android.app.Application
import com.trainingtimer.data.TrainingRepositoryImpl

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TrainingRepositoryImpl.initialize(this)
    }
}