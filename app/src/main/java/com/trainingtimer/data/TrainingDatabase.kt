package com.trainingtimer.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TrainingDbEntity::class])
abstract class TrainingDatabase : RoomDatabase() {
}