package com.trainingtimer.domain

import kotlinx.coroutines.flow.Flow

interface TrainingRepository {

    fun addTraining(training: Training)

    fun deleteTraining(training: Training)

    fun editTraining(training: Training)

    fun getTraining(trainingId: Int): Flow<Training?>

    fun getTrainingList(): Flow<List<Training>>

}