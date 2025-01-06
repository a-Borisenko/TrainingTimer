package com.trainingtimer.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrainingListUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun getTrainingList(): Flow<List<Training>> {
        return trainingRepository.getTrainingList()
    }
}