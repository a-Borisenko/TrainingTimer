package com.trainingtimer.domain

class GetTrainingUseCase(private val trainingListRepository: TrainingListRepository) {

    fun getTraining(trainingId: Int): Training {
        return trainingListRepository.getTraining(trainingId)
    }
}