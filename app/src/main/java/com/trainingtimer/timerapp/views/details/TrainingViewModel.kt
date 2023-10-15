package com.trainingtimer.timerapp.views.details

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.foundation.data.TrainingRepositoryImpl
import com.trainingtimer.foundation.domain.AddTrainingUseCase
import com.trainingtimer.foundation.domain.EditTrainingUseCase
import com.trainingtimer.foundation.domain.GetTrainingListUseCase
import com.trainingtimer.foundation.domain.GetTrainingUseCase
import com.trainingtimer.foundation.domain.Training
import com.trainingtimer.timerapp.views.details.TrainingFragment.Companion.secondsRemaining
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrainingViewModel : ViewModel() {

    private val repository = TrainingRepositoryImpl.get()
    private val getTrainingUseCase = GetTrainingUseCase(repository)
    private val addTrainingUseCase = AddTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)
    private val getTrainingListUseCase = GetTrainingListUseCase(repository)

    lateinit var trainingLD: LiveData<Training?>
    lateinit var trainNumber: LiveData<List<Training>>

    private val _errorInputTimes = MutableLiveData<Boolean>()
    val errorInputTimes: LiveData<Boolean>
        get() = _errorInputTimes

    private val _errorInputTitle = MutableLiveData<Boolean>()
    val errorInputTitle: LiveData<Boolean>
        get() = _errorInputTitle

    private val _errorInputSets = MutableLiveData<Boolean>()
    val errorInputSets: LiveData<Boolean>
        get() = _errorInputSets

    private val _training = MutableLiveData<Training>()
    val training: LiveData<Training>
        get() = _training

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    private val _secRem = MutableLiveData<Long>()
    val secRem: LiveData<Long>
        get() = _secRem

    fun getTraining(trainingId: Int) {
        val item = getTrainingUseCase.getTraining(trainingId)
        trainingLD = item
    }

    fun getTrainNumber() {
        trainNumber = getTrainingListUseCase.getTrainingList()
    }

    fun addTraining(
        inputSets: Int?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?,
        trainingId: Int
    ) {
        viewModelScope.launch {
            delay(3000)
            val times = parseTimes(inputTimes)
            val title = parseTitle(inputTitle)
            val sets = parseSets(inputSets)
            val rest = parseRest(inputRest)
            val fieldValid = validateInput(sets, title, times)
            if (fieldValid) {
                val training = Training(sets, title, times, rest, trainingId)
                addTrainingUseCase.addTraining(training)
                finishWork()
            }
        }
    }

    fun editTraining(
        inputSets: Int?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?,
        trainingId: Int
    ) {
        viewModelScope.launch {
            delay(3000)
            val times = parseTimes(inputTimes)
            val title = parseTitle(inputTitle)
            val sets = parseSets(inputSets)
            val rest = parseRest(inputRest)
            val fieldValid = validateInput(sets, title, times)
            if (fieldValid) {
                val item = Training(sets, title, times, rest, trainingId)
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

    private fun validateInput(sets: Int, title: String, times: String): Boolean {
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

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }

    object Timer : CountDownTimer(secondsRemaining * 1000, 1000) {
        override fun onFinish() {
            Log.d("new Timer", "done!!!")
        }

        override fun onTick(millisUntilFinished: Long) {
            secondsRemaining = millisUntilFinished / 1000
            Log.d("new Timer", secondsRemaining.toString())
        }
    }
}