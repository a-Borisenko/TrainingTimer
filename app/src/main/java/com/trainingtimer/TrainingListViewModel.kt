package com.trainingtimer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrainingListViewModel : ViewModel() {

    private val trainingRepository = TrainingRepository.get()
    val trainingListLiveData = trainingRepository.getTrainings()

    fun addTraining(training: Training) {
        trainingRepository.addTraining(training)
    }

    val trainingList = MutableLiveData<List<Training>>()

    fun getTrainingList() {
        trainingList.value = TODO() //getTrainingListUseCase.getTrainingList()
    }
}