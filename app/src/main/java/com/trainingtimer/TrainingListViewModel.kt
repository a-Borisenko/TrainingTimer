package com.trainingtimer

import androidx.lifecycle.ViewModel
import java.util.*

class TrainingListViewModel : ViewModel() {

    /*val trainings = mutableListOf<Training>()

    init {
        for (i in 0 until 100) {
            val training = Training(UUID.randomUUID(), "", 0, 0)
            training.title = "Training #$i"
            trainings += training
        }
    }*/

    private val trainingRepository = TrainingRepository.get()
    val trainingListLiveData = trainingRepository.getTrainings()
}