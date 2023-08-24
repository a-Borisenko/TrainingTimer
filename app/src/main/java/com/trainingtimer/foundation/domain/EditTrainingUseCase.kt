package com.trainingtimer.foundation.domain

class EditTrainingUseCase(private val trainingRepository: TrainingRepository) {

    fun editTraining(training: Training) {
        trainingRepository.editTraining(training)
    }
}