package com.trainingtimer.domain

import javax.inject.Inject

class AddTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun addTraining(training: Training) {
        trainingRepository.addTraining(training)
    }
}