package com.trainingtimer.domain.usecases

import com.trainingtimer.domain.repository.TrainingRepository
import com.trainingtimer.domain.entity.Training
import javax.inject.Inject

class EditTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun editTraining(training: Training) {
        trainingRepository.editTraining(training)
    }
}