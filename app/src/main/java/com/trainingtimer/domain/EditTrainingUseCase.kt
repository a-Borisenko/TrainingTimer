package com.trainingtimer.domain

import javax.inject.Inject

class EditTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun editTraining(training: Training) {
        trainingRepository.editTraining(training)
    }
}