package com.trainingtimer.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.trainingtimer.domain.Training
import com.trainingtimer.domain.TrainingRepository
import java.util.concurrent.Executors

private const val DATABASE_NAME = "training-database"

class TrainingRepositoryImpl private constructor(context: Context) : TrainingRepository {

    private val database: TrainingDatabase = Room.databaseBuilder(
        context.applicationContext,
        TrainingDatabase::class.java,
        DATABASE_NAME
    )
        .createFromAsset("initial_database.db")
        .build()

    private val trainingDao = database.trainingDao()
    private val executor = Executors.newSingleThreadExecutor()

    private var autoIncrementId = 0

    override fun addTraining(training: Training) {
        if (training.id == Training.UNDEFINED_ID) {
            training.id = autoIncrementId++
        }
        executor.execute {
            trainingDao.addTraining(training)
        }
    }

    override fun deleteTraining(training: Training) {
        executor.execute {
            trainingDao.deleteTraining(training)
        }
    }

    override fun editTraining(training: Training) {
        executor.execute {
            trainingDao.updateTraining(training)
        }
    }

    override fun getTraining(trainingId: Int): LiveData<Training?> {
        return trainingDao.getTraining(trainingId)
    }

    override fun getTrainingList(): LiveData<List<Training>> {
        return trainingDao.getTrainings()
    }

    companion object {
        private var INSTANCE: TrainingRepositoryImpl? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TrainingRepositoryImpl(context)
            }
        }

        fun get(): TrainingRepositoryImpl {
            return INSTANCE
                ?: throw IllegalStateException("TrainingRepositoryImpl must be initialized")
        }
    }
}