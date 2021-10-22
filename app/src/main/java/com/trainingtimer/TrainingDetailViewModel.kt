package com.trainingtimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class TrainingDetailViewModel() : ViewModel() {

    private val trainingRepository = TrainingRepository.get()
    private val trainingIdLiveData = MutableLiveData<UUID>()

    var trainingLiveData: LiveData<Training?> =
        Transformations.switchMap(trainingIdLiveData) { trainingId ->
            trainingRepository.getTraining(trainingId)
        }

    fun loadTraining(trainingId: UUID) {
        trainingIdLiveData.value = trainingId
    }

    fun saveTraining(training: Training) {
        trainingRepository.updateTraining(training)
    }
}