package com.trainingtimer.domain

import androidx.lifecycle.LiveData

interface TrainingRepository {

    fun addTraining(training: Training)

    fun deleteTraining(training: Training)

    fun editTraining(training: Training)

    fun getTraining(trainingId: Int): LiveData<Training?>

//    fun loadAllByIds(id: IntArray): List<Training>

    fun getTrainingList(): LiveData<List<Training>>
}