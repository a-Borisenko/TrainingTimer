package com.trainingtimer.ui

import androidx.lifecycle.ViewModel
import com.trainingtimer.data.TrainingRepositoryImpl
import com.trainingtimer.domain.DeleteTrainingUseCase
import com.trainingtimer.domain.EditTrainingUseCase
import com.trainingtimer.domain.GetTrainingListUseCase
import com.trainingtimer.domain.Training

class TrainingListViewModel : ViewModel() {

    //import data.TrainingListRepositoryImpl

//    private val trainingRepository = TrainingRepository.get()
    private val repository = TrainingRepositoryImpl

//    val trainingListLiveData = trainingRepository.getTrainings()
    private val getTrainingListUseCase = GetTrainingListUseCase(repository.get())
    private val deleteTrainingUseCase = DeleteTrainingUseCase(repository.get())
    private val editTrainingUseCase = EditTrainingUseCase(repository.get())

    val trainingList = getTrainingListUseCase.getTrainingList() //MutableLiveData<List<Training>>()

    fun deleteTraining(training: Training) {
        deleteTrainingUseCase.deleteTraining(training)
    }

//    fun addTraining(training: Training) {
//        trainingRepository.addTraining(training)
//    }

    /*fun getTrainingList() {
        trainingList.value = getTrainingListUseCase.getTrainingList()
    }*/
}