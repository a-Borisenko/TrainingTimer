package com.trainingtimer.domain

import androidx.lifecycle.LiveData
import javax.inject.Inject

class GetTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun getTraining(trainingId: Int): LiveData<Training?> {
        return trainingRepository.getTraining(trainingId)
    }
}