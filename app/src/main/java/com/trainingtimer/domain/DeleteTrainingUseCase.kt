package com.trainingtimer.domain

import javax.inject.Inject

class DeleteTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun deleteTraining(training: Training) {
        trainingRepository.deleteTraining(training)
    }
}