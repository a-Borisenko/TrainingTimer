package com.trainingtimer

import android.app.Application
import android.content.Context
import com.trainingtimer.data.TrainingRepositoryImpl
import com.trainingtimer.utils.NotificationManager
import com.trainingtimer.utils.NotificationManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TrainingRepositoryImpl.initialize(this)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object AppModule {
        @Provides
        @Singleton
        fun provideNotificationManager(context: Context): NotificationManager {
            return NotificationManagerImpl(context)
        }
    }
}