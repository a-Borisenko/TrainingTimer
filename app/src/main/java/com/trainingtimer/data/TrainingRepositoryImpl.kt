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
    ).build()

    private var autoIncrementId = 0

    private val trainingDao = database.trainingDao()
    private val executor = Executors.newSingleThreadExecutor()

    init {
        addTraining(Training(1, "подтягивания", "x5", "01:00"))
        addTraining(Training(1, "отжимания", "x10", "01:00"))
        addTraining(Training(1, "приседания", "x15", "01:00"))
        for (i in 4 until 100) {
            val item = Training(i, "Training №$i", "x$i", "01:00")
            addTraining(item)
        }
    }

    override fun addTraining(training: Training) {
        if (training.id == Training.UNDEFINED_ID) {
            training.id = autoIncrementId++
        }
        executor.execute {
            trainingDao.addTraining(training)   //pool-2-thread-1 Room cannot verify the data integrity.
        // Looks like you've changed schema but forgot to update the version number.
        // You can simply fix this by increasing the version number.
        }
    }

    override fun deleteTraining(training: Training) {
        executor.execute {
            trainingDao.deleteTraining(training)
        }
    }

    override fun editTraining(training: Training) {
        /*executor.execute {
            val oldElement = getTraining(training.id).value
                ?: throw IllegalStateException("Training with id${training.id} not found")
            trainingDao.deleteTraining(oldElement)
            trainingDao.addTraining(training)
        }*/
    }

    override fun getTraining(trainingId: Int): LiveData<Training?> {
        return trainingDao.getTraining(trainingId)
    }

    override fun getTrainingList(): LiveData<List<Training>> {
        return trainingDao.getTrainings()
    }

    private fun updateTraining(training: Training) {
        executor.execute {
            trainingDao.updateTraining(training)
        }
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