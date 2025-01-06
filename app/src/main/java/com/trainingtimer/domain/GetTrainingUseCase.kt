package com.trainingtimer.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun getTraining(trainingId: Int): Flow<Training?> {
        return trainingRepository.getTraining(trainingId)
    }
}