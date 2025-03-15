package com.trainingtimer.domain.repository

import com.trainingtimer.data.database.TrainingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTrainingRepository(): TrainingRepository {
        return TrainingRepositoryImpl()
    }
}