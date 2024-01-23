package com.trainingtimer.timerapp

import android.app.Application
import com.trainingtimer.timerapp.data.TrainingRepositoryImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TrainingRepositoryImpl.initialize(this)
    }
}