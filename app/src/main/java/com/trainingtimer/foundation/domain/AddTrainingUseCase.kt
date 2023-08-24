package com.trainingtimer.foundation.domain

class AddTrainingUseCase(private val trainingRepository: TrainingRepository) {

    fun addTraining(training: Training) {
        trainingRepository.addTraining(training)
    }
}