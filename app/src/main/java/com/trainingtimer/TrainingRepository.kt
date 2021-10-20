package com.trainingtimer

import android.content.Context
import androidx.room.Room
import com.trainingtimer.database.TrainingDatabase
import java.util.*

private const val DATABASE_NAME = "training-database"

class TrainingRepository private constructor(context: Context){

    private val database: TrainingDatabase = Room.databaseBuilder(
        context.applicationContext,
        TrainingDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val trainingDao = database.trainingDao()

    fun getTrainings(): List<Training> = trainingDao.getTrainings()

    fun getTraining(id: UUID): Training? = trainingDao.getTraining(id)

    companion object {
        private var INSTANCE: TrainingRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TrainingRepository(context)
            }
        }

        fun get() : TrainingRepository {
            return INSTANCE ?:
            throw IllegalStateException("TrainingRepository must be initialized")
        }
    }
}