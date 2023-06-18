package com.trainingtimer.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.trainingtimer.domain.Training
import com.trainingtimer.domain.TrainingListRepository

private const val DATABASE_NAME = "training-database"

object TrainingListRepositoryImpl : TrainingListRepository {

    private val database : TrainingDatabase = Room.databaseBuilder(
        this.context,
        TrainingDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val trainingListLD = MutableLiveData<List<Training>>()
    private val trainingList = sortedSetOf<Training>({ p0, p1 -> p0.id.compareTo(p1.id) }) //временное хранение в переменной вместо БД
    private val trainingDao = database.trainingDao()
    private var autoIncrementId = 0

    init {
        addTraining(Training(1, "подтягивания", "x5", "01:00"))
        addTraining(Training(1, "отжимания", "x10", "01:00"))
        addTraining(Training(1, "приседания", "x15", "01:00"))
        for (i in 4 until 100) {
            val item = Training(i,"Training №$i", "x$i", "01:00")
            addTraining(item)
        }
    }

    override fun addTraining(training: Training) {
        if (training.id == Training.UNDEFINED_ID) {
            training.id = autoIncrementId++
        }
//        trainingDao.addTraining(TrainingDbEntity.fromUser(training))
        trainingList.add(training)
        updateList()
    }

    override fun deleteTraining(training: Training) {
//        trainingDao.deleteTraining(TrainingDbEntity.fromUser(training))
        trainingList.remove(training)
        updateList()
    }

    override fun editTraining(training: Training) {
        val oldElement = getTraining(training.id)
        trainingList.remove(oldElement)
        addTraining(training)
    }

    override fun getTraining(trainingId: Int): Training {
        return trainingList.find { it.id == trainingId }
            ?: throw RuntimeException("element with id$trainingId not found")
    }

    override fun getTrainingList(): LiveData<List<Training>> {
        return trainingListLD
    }

    private fun updateList() {
        trainingDao.updateTraining()
        trainingListLD.value = trainingList.toList()
    }
}