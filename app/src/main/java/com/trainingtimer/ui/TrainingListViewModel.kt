package com.trainingtimer.ui

import androidx.lifecycle.ViewModel
import com.trainingtimer.data.TrainingListRepositoryImpl
import com.trainingtimer.domain.DeleteTrainingUseCase
import com.trainingtimer.domain.EditTrainingUseCase
import com.trainingtimer.domain.GetTrainingListUseCase
import com.trainingtimer.domain.Training

class TrainingListViewModel : ViewModel() {

    //import data.TrainingListRepositoryImpl

//    private val trainingRepository = TrainingRepository.get()
    private val repository = TrainingListRepositoryImpl

//    val trainingListLiveData = trainingRepository.getTrainings()
    private val getTrainingListUseCase = GetTrainingListUseCase(repository)
    private val deleteTrainingUseCase = DeleteTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)

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