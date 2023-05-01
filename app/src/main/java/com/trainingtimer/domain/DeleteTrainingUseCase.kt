package com.trainingtimer.domain

class DeleteTrainingUseCase(private val trainingListRepository: TrainingListRepository) {

    fun deleteTraining(training: Training) {
        trainingListRepository.deleteTraining(training)
    }
}