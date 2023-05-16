package com.trainingtimer.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trainingtimer.data.TrainingListRepositoryImpl
import com.trainingtimer.data.TrainingRepository
import com.trainingtimer.domain.DeleteTrainingUseCase
import com.trainingtimer.domain.Training

class TrainingListViewModel : ViewModel() {

    private val trainingRepository = TrainingRepository.get()
    private val repository = TrainingListRepositoryImpl

    val trainingListLiveData = trainingRepository.getTrainings()
    private val deleteTrainingUseCase = DeleteTrainingUseCase(repository)

    fun addTraining(training: Training) {
        trainingRepository.addTraining(training)
    }

    val trainingList = MutableLiveData<List<Training>>()

    fun getTrainingList() {
        trainingList.value = TODO() //getTrainingListUseCase.getTrainingList()
    }

    fun deleteTraining(training: Training) {
        deleteTrainingUseCase.deleteTraining(training)
    }
}