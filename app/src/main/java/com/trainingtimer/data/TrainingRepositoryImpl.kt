package com.trainingtimer.data

import android.content.Context
import androidx.room.Room
import com.trainingtimer.domain.entity.Training
import com.trainingtimer.domain.repository.TrainingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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

    init {
        CoroutineScope(Dispatchers.IO).launch {
            autoIncrementId = trainingDao.getTrainings()
                .firstOrNull()?.size ?: 0
        }
    }

    override fun addTraining(training: Training) {
        if (training.id == Training.UNDEFINED_ID) {
            training.id = ++autoIncrementId
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

    @Provides
    override fun getTraining(trainingId: Int): Flow<Training?> {
        return trainingDao.getTraining(trainingId)
    }

    override fun getTrainingList(): Flow<List<Training>> {
        return trainingDao.getTrainings()
    }


    companion object {
        private const val DATABASE_NAME = "training-database"
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