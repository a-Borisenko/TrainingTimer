package com.trainingtimer.domain

import androidx.lifecycle.LiveData

class GetTrainingListUseCase(private val trainingRepository: TrainingRepository) {

    fun getTrainingList(): LiveData<List<Training>> {
        return trainingRepository.getTrainingList()
    }
}