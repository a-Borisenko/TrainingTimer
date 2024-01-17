package com.trainingtimer.timerapp.domain

class AddTrainingUseCase(private val trainingRepository: TrainingRepository) {

    fun addTraining(training: Training) {
        trainingRepository.addTraining(training)
    }
}