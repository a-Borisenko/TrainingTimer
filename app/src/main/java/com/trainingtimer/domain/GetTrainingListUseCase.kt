package com.trainingtimer.domain

import androidx.lifecycle.LiveData

class GetTrainingListUseCase(private val trainingListRepository: TrainingListRepository) {

    fun getTrainingList(): LiveData<List<Training>> {
        return trainingListRepository.getTrainingList()
    }
}