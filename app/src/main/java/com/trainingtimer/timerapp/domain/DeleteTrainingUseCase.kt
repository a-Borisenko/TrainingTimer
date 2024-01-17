package com.trainingtimer.timerapp.domain

class DeleteTrainingUseCase(private val trainingRepository: TrainingRepository) {

    fun deleteTraining(training: Training) {
        trainingRepository.deleteTraining(training)
    }
}