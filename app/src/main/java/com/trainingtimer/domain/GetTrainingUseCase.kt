package com.trainingtimer.domain

import androidx.lifecycle.LiveData

class GetTrainingUseCase(private val trainingRepository: TrainingRepository) {

    fun getTraining(trainingId: Int): LiveData<Training?> {
        return trainingRepository.getTraining(trainingId)
    }
}