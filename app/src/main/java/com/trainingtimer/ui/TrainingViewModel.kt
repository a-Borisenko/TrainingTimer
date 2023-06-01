package com.trainingtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.trainingtimer.data.TrainingListRepositoryImpl
import com.trainingtimer.domain.GetTrainingUseCase
import com.trainingtimer.domain.Training
import java.util.*

class TrainingViewModel() : ViewModel() {

    private val repository = TrainingListRepositoryImpl
//    private val trainingRepository = TrainingRepository.get()
    private val trainingIdLiveData = MutableLiveData<UUID>()
    private val getTrainingUseCase = GetTrainingUseCase(repository)

    private val _training = MutableLiveData<Training>()
    val training: LiveData<Training>
        get() = _training

    /*var trainingLiveData: LiveData<Training?> =
        Transformations.switchMap(trainingIdLiveData) { trainingId ->
            trainingRepository.getTraining(trainingId)
        }*/

    fun loadTraining(trainingId: UUID) {
        trainingIdLiveData.value = trainingId
    }

    /*fun saveTraining(training: Training) {
        trainingRepository.updateTraining(training)
    }*/

    fun getTraining(trainingId: Int) {
        val item = getTrainingUseCase.getTraining(trainingId)
        _training.value = item
    }
}