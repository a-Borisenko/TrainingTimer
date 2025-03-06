package com.trainingtimer.domain.usecases

import com.trainingtimer.domain.repository.TrainingRepository
import com.trainingtimer.domain.entity.Training
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrainingListUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    suspend fun getTrainingList(): Flow<List<Training>> {
        return trainingRepository.getTrainingList()
    }
}