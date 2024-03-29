package com.trainingtimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trainingtimer.data.TrainingRepositoryImpl
import com.trainingtimer.domain.AddTrainingUseCase
import com.trainingtimer.domain.EditTrainingUseCase
import com.trainingtimer.domain.GetTrainingUseCase
import com.trainingtimer.domain.Training

class TrainingViewModel : ViewModel() {

    private val repository = TrainingRepositoryImpl.get()
    private val getTrainingUseCase = GetTrainingUseCase(repository)
    private val addTrainingUseCase = AddTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)

    lateinit var trainingLD: LiveData<Training?>

    private val _errorInputTimes = MutableLiveData<Boolean>()
    val errorInputTimes: LiveData<Boolean>
        get() = _errorInputTimes

    private val _errorInputTitle = MutableLiveData<Boolean>()
    val errorInputTitle: LiveData<Boolean>
        get() = _errorInputTitle

    private val _errorInputSets = MutableLiveData<Boolean>()
    val errorInputSets: LiveData<Boolean>
        get() = _errorInputSets

    private val _errorInputRest = MutableLiveData<Boolean>()
    val errorInputRest: LiveData<Boolean>
        get() = _errorInputRest

    private val _training = MutableLiveData<Training>()
    val training: LiveData<Training>
        get() = _training

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun getTraining(trainingId: Int) {
        val item = getTrainingUseCase.getTraining(trainingId)
//        Log.d("TrainingViewModel", "Training id $trainingId")
//        Log.d("TrainingViewModel", "Item value = $item")
        trainingLD = item
        /*item.observe(this) {
            _training.value = it
        }*/
//            ?: throw IllegalStateException("Training with id${trainingId} not found")
    }

    fun addTraining(
        inputSets: Int?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?
    ) {
        val times = parseTimes(inputTimes)
        val title = parseTitle(inputTitle)
        val sets = parseSets(inputSets)
        val rest = parseRest(inputRest)
        val fieldValid = validateInput(sets, title, times, rest)
        if (fieldValid) {
            val training = Training(sets, title, times, rest)
            addTrainingUseCase.addTraining(training)
            finishWork()
        }
    }

    fun editTraining(
        inputSets: Int?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?
    ) {
        val times = parseTimes(inputTimes)
        val title = parseTitle(inputTitle)
        val sets = parseSets(inputSets)
        val rest = parseRest(inputRest)
        val fieldValid = validateInput(sets, title, times, rest)
        if (fieldValid) {
            _training.value?.let {
                val item = it.copy(sets = sets, title = title, times = times, rest = rest)
                editTrainingUseCase.editTraining(item)
                finishWork()
            }
        }
    }

    private fun parseSets(inputSets: Int?) = inputSets?.toString()?.trim()?.toInt()
        ?: Training.UNDEFINED_ID

    private fun parseTitle(inputTitle: String?) = inputTitle?.trim() ?: ""

    private fun parseTimes(inputTimes: String?) = inputTimes?.trim() ?: ""

    private fun parseRest(inputRest: String?) = inputRest?.trim() ?: ""

    private fun validateInput(sets: Int, title: String, times: String, rest: String): Boolean {
        var res = true
        if (times.isBlank()) {
            _errorInputTimes.value = true
            res = false
        }
        if (title.isBlank()) {
            _errorInputTitle.value = true
            res = false
        }
        if (sets.toString().isBlank()) {
            _errorInputSets.value = true
            res = false
        }
        if (rest.isBlank()) {
            _errorInputRest.value = true
            res = false
        }
        return res
    }

    fun resetErrorInputTimes() {
        _errorInputTimes.value = false
    }

    fun resetErrorInputTitle() {
        _errorInputTitle.value = false
    }

    fun resetErrorInputSets() {
        _errorInputSets.value = false
    }

    fun resetErrorInputRest() {
        _errorInputRest.value = false
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}