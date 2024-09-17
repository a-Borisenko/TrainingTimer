package com.trainingtimer.domain

import androidx.lifecycle.LiveData
import javax.inject.Inject

class GetTrainingListUseCase @Inject constructor(private val trainingRepository: TrainingRepository) {

    fun getTrainingList(): LiveData<List<Training>> {
        return trainingRepository.getTrainingList()
    }
}