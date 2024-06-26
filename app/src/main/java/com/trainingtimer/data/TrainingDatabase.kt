package com.trainingtimer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trainingtimer.domain.Training

@Database(version = 1, entities = [Training::class])
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

}