package com.trainingtimer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trainingtimer.domain.Training

@Database(entities = [ Training::class], version = 1)
@TypeConverters(TrainingTypeConverters::class)
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao
}