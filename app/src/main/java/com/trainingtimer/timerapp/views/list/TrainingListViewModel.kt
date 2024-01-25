package com.trainingtimer.timerapp.views.list

import androidx.lifecycle.ViewModel
import com.trainingtimer.timerapp.data.TrainingRepositoryImpl
import com.trainingtimer.timerapp.domain.DeleteTrainingUseCase
import com.trainingtimer.timerapp.domain.GetTrainingListUseCase
import com.trainingtimer.timerapp.domain.Training
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class TrainingListViewModel : ViewModel() {

    //import data.TrainingListRepositoryImpl

    private val repository = TrainingRepositoryImpl

    private val getTrainingListUseCase = GetTrainingListUseCase(repository.get())
    private val deleteTrainingUseCase = DeleteTrainingUseCase(repository.get())

    val trainingList = getTrainingListUseCase.getTrainingList()

    fun deleteTraining(training: Training) {
        deleteTrainingUseCase.deleteTraining(training)
    }
}