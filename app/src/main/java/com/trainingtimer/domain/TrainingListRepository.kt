package com.trainingtimer.domain

import androidx.lifecycle.LiveData

interface TrainingListRepository {

    fun addTraining(training: Training)

    fun deleteTraining(training: Training)

    fun editTraining(training: Training)

    fun getTraining(trainingId: Int): Training

    fun getTrainingList(): LiveData<List<Training>>
}