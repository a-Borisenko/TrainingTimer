package com.trainingtimer.domain

class EditTrainingUseCase(private val trainingListRepository: TrainingListRepository) {

    fun editTraining(training: Training) {
        trainingListRepository.editTraining(training)
    }
}