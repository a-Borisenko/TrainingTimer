package com.trainingtimer.views.list

import androidx.lifecycle.ViewModel
import com.trainingtimer.data.TrainingRepositoryImpl
import com.trainingtimer.domain.DeleteTrainingUseCase
import com.trainingtimer.domain.GetTrainingListUseCase
import com.trainingtimer.domain.Training
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    rep: TrainingRepositoryImpl
) : ViewModel() {

    //import data.TrainingListRepositoryImpl

//    private val repository = TrainingRepositoryImpl

    private val getTrainingListUseCase = GetTrainingListUseCase(rep.getRep())
    private val deleteTrainingUseCase = DeleteTrainingUseCase(rep.getRep())

    val trainingList = getTrainingListUseCase.getTrainingList()

    fun deleteTraining(training: Training) {
        deleteTrainingUseCase.deleteTraining(training)
    }
}