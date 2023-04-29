package com.trainingtimer.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trainingtimer.TrainingRepository
import com.trainingtimer.domain.Training

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