package com.trainingtimer.ui.list

import androidx.lifecycle.ViewModel
import com.trainingtimer.data.TrainingRepositoryImpl
import com.trainingtimer.domain.DeleteTrainingUseCase
import com.trainingtimer.domain.EditTrainingUseCase
import com.trainingtimer.domain.GetTrainingListUseCase
import com.trainingtimer.domain.Training

class TrainingListViewModel : ViewModel() {

    //import data.TrainingListRepositoryImpl

    /*init {
        addTraining(Training(1, "подтягивания", "x5", "01:00"))
        addTraining(Training(1, "отжимания", "x10", "01:00"))
        addTraining(Training(1, "приседания", "x15", "01:00"))
        for (i in 4 until 100) {
            val item = Training(i, "Training №$i", "x$i", "01:00")
            addTraining(item)
        }
    }*/

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