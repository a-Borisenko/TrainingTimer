package com.trainingtimer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trainingtimer.domain.Training
import com.trainingtimer.views.details.DATABASE_NAME

@Database(version = 1, entities = [Training::class])
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var inastance: TrainingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): TrainingDatabase {
            return inastance ?: synchronized(LOCK) {
                inastance ?: buildDatabase(context).also {
                    inastance = it
                }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, TrainingDatabase::class.java, DATABASE_NAME)
                .createFromAsset("initial_database.db")
                .build()
    }
}