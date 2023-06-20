package com.trainingtimer.domain

class GetTrainingUseCase(private val trainingRepository: TrainingRepository) {

    fun getTraining(trainingId: Int): Training {
        return trainingRepository.getTraining(trainingId)
    }
}