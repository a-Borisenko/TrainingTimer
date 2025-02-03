package com.trainingtimer.domain.usecases

import com.trainingtimer.domain.repository.TrainingRepository
import com.trainingtimer.domain.entity.Training
import javax.inject.Inject

class DeleteTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun deleteTraining(training: Training) {
        trainingRepository.deleteTraining(training)
    }
}