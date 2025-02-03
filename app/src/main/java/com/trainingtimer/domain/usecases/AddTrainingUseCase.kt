package com.trainingtimer.domain.usecases

import com.trainingtimer.domain.repository.TrainingRepository
import com.trainingtimer.domain.entity.Training
import javax.inject.Inject

class AddTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun addTraining(training: Training) {
        trainingRepository.addTraining(training)
    }
}