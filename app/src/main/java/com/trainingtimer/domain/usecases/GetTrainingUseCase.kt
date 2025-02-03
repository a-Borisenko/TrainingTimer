package com.trainingtimer.domain.usecases

import com.trainingtimer.domain.repository.TrainingRepository
import com.trainingtimer.domain.entity.Training
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrainingUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun getTraining(trainingId: Int): Flow<Training?> {
        return trainingRepository.getTraining(trainingId)
    }
}