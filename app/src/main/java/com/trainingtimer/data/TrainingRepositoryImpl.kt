package com.trainingtimer.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val trainingListLD = MutableLiveData<List<Training>>()
    private val trainingList = sortedSetOf<Training>({ p0, p1 -> p0.id.compareTo(p1.id) }) //временное хранение в переменной вместо БД
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
            trainingDao.addTraining(training)
        }
//        trainingList.add(training)
//        updateList()
    }

    override fun deleteTraining(training: Training) {
        executor.execute {
            trainingDao.deleteTraining(training)
        }
//        trainingList.remove(training)
//        updateList()
    }

    override fun editTraining(training: Training) {
        executor.execute {
            val oldElement = getTraining(training.id).value
                ?: throw IllegalStateException("Training with id${training.id} not found")
            trainingDao.deleteTraining(oldElement)
            trainingDao.addTraining(training)
        }
//        trainingList.remove(oldElement)
//        addTraining(training)
    }

    override fun getTraining(trainingId: Int): LiveData<Training?> {
        return trainingDao.getTraining(trainingId)
//        return trainingList.find { it.id == trainingId }
            ?: throw RuntimeException("element with id$trainingId not found")
    }

    override fun getTrainingList(): LiveData<List<Training>> {
        return trainingDao.getTrainings()
//        return trainingListLD
    }

    /*private fun updateList() {
        trainingDao.getTrainings().value = trainingList.toList()
        trainingListLD.value = trainingList.toList()
    }*/

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