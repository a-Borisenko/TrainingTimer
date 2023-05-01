package com.trainingtimer.domain

class AddTrainingUseCase(private val trainingListRepository: TrainingListRepository) {

    fun addTraining(training: Training) {
        trainingListRepository.addTraining(training)
    }
}