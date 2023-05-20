package com.trainingtimer.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trainingtimer.domain.Training
import com.trainingtimer.domain.TrainingListRepository

object TrainingListRepositoryImpl : TrainingListRepository {

    private val trainingListLD = MutableLiveData<List<Training>>()
    private val trainingList = sortedSetOf<Training>({ p0, p1 -> p0.id.compareTo(p1.id) }) //временное хранение в переменной вместо БД
    private var autoIncrementId = 0

    init {
        for (i in 0 until 1000) {
            val item = Training("$i","Training №$i", "x$i", "01:00")
            addTraining(item)
        }
    }

    override fun addTraining(training: Training) {
        if (training.id == Training.UNDEFINED_ID) {
            training.id = autoIncrementId++
        }
        trainingList.add(training)
        updateList()
    }

    override fun deleteTraining(training: Training) {
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
        trainingListLD.value = trainingList.toList()
    }
}