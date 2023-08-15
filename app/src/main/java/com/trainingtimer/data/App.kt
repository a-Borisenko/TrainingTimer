package com.trainingtimer.data

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TrainingRepositoryImpl.initialize(this)
    }
}