package com.trainingtimer.foundation.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.trainingtimer.foundation.domain.Training
import com.trainingtimer.foundation.domain.TrainingRepository
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

    private val trainingDao = database.trainingDao()  //TrainingDatabase.database(context).trainingDao()
    private val executor = Executors.newSingleThreadExecutor()

    private var autoIncrementId = 0    //for init only

    /*init {
        //TODO: init must be only if there is no DataBase on the phone yet
        addTraining(Training(1, "подтягивания", "x5", "01:00"))
        addTraining(Training(1, "отжимания", "x10", "01:00"))
        addTraining(Training(1, "приседания", "x15", "01:00"))
        for (i in 4 until 100) {
            val item = Training(i, "Training №$i", "x$i", "01:00")
            addTraining(item)
        }
    }*/

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