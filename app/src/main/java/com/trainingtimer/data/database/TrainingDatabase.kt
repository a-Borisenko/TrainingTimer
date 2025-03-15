package com.trainingtimer.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trainingtimer.data.entity.Training

@Database(version = 1, entities = [Training::class])
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    companion object {

        private var INSTANCE: TrainingDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = ""

        fun getInstance(application: Application): TrainingDatabase {

            INSTANCE?.let {
                return it
            }

            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }

                val db = Room.databaseBuilder(
                    application,
                    TrainingDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = db
                return db
            }
        }
    }
}