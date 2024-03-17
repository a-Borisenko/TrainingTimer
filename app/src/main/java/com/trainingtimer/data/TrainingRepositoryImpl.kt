package com.trainingtimer.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.trainingtimer.domain.Training
import com.trainingtimer.domain.TrainingRepository
import com.trainingtimer.views.details.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import java.util.concurrent.Executors
import javax.inject.Inject

@DisableInstallInCheck
@Module
class TrainingRepositoryImpl @Inject constructor() : TrainingRepository {

    private val database: TrainingDatabase = Room.databaseBuilder(
        appContext.applicationContext,
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

    @Provides
    fun getRep(): TrainingRepositoryImpl {
        return INSTANCE
            ?: throw IllegalStateException("TrainingRepositoryImpl must be initialized")
    }

    companion object {
        private lateinit var appContext: Context
        private var INSTANCE: TrainingRepositoryImpl? = null

        fun initialize(context: Context) {
            appContext = context
            if (INSTANCE == null) {
                INSTANCE = TrainingRepositoryImpl()
            }
        }
    }
}