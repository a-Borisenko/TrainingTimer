package com.trainingtimer.timerapp.views.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.foundation.data.TrainingRepositoryImpl
import com.trainingtimer.foundation.domain.AddTrainingUseCase
import com.trainingtimer.foundation.domain.EditTrainingUseCase
import com.trainingtimer.foundation.domain.GetTrainingListUseCase
import com.trainingtimer.foundation.domain.GetTrainingUseCase
import com.trainingtimer.foundation.domain.Training
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrainingViewModel : ViewModel() {

    private val repository = TrainingRepositoryImpl.get()
    private val getTrainingUseCase = GetTrainingUseCase(repository)
    private val addTrainingUseCase = AddTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)
    private val getTrainingListUseCase = GetTrainingListUseCase(repository)

    private var saveState = false
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

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _secRemain = MutableLiveData<Long>()
    val secRemain: LiveData<Long>
        get() = _secRemain

    private val _progress = MutableLiveData<Float>()
    val progress: LiveData<Float>
        get() = _progress

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    private val _sets = MutableLiveData<Int>()
    val sets: LiveData<Int>
        get() = _sets

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _times = MutableLiveData<String>()
    val times: LiveData<String>
        get() = _times



    private val trainingData = Observer<Training?> {
        if (!saveState) {
            _sets.value = it.sets
            _title.value = it.title
            _times.value = it.times
            _secRemain.value = TrainingUtils.timeStringToLong(it.rest)
        }
    }

    private val serviceTime = Observer<Long> {
        _secRemain.value = it
    }

    private val serviceProgress = Observer<Float> {
        _progress.value = it
    }



    fun start(id: Int) {
        TimerService.secRemainLD.observeForever(serviceTime)
        TimerService.progressLD.observeForever(serviceProgress)
        resetProgress()

        launchMode(id)
    }

    override fun onCleared() {
        super.onCleared()
        TimerService.secRemainLD.removeObserver(serviceTime)
        TimerService.progressLD.removeObserver(serviceProgress)
    }

    fun saveState(sets: String, title: String, times: String) {
        saveState = true
        _sets.value = sets.toInt()
        _title.value = title
        _times.value = times
    }

    fun getTrainingNumber() {
        trainNumber = getTrainingListUseCase.getTrainingList()
    }

    fun updateTime(sec: Long) {
        _secRemain.value = sec
        Log.d("viewModel", "sec remain = $sec")
    }

    fun resetProgress() {
        _progress.value = 100f
    }

    private fun launchMode(id: Int) {
        if (id != Training.UNDEFINED_ID) {
            getTrainingUseCase.getTraining(id).observeForever(trainingData)
        }
    }

    /*fun getTime() {
        //get time from DataBase
    }

    fun getDialogTime() {
        //get time from dialogFragment
    }

    fun startTimer() {
        //start TimerService
    }

    fun stopTimer() {
        //stop TimerService
    }*/

    fun addTraining(
        inputSets: String?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?,
        trainingId: Int
    ) {
        val sets = parseSets(inputSets)
        val title = parseTitle(inputTitle)
        val times = parseTimes(inputTimes)
        val rest = parseRest(inputRest)
        val fieldValid = validateInput(sets, title, times)
        if (fieldValid) {
            viewModelScope.launch {
                startLoad()
                delay(3000)
                finishLoad()
                val training = Training(sets.toInt(), title, times, rest, trainingId)
                addTrainingUseCase.addTraining(training)
                finishWork()
            }
        }
    }

    fun editTraining(
        inputSets: String?,
        inputTitle: String?,
        inputTimes: String?,
        inputRest: String?,
        trainingId: Int
    ) {
        val sets = parseSets(inputSets)
        val title = parseTitle(inputTitle)
        val times = parseTimes(inputTimes)
        val rest = parseRest(inputRest)
        val fieldValid = validateInput(sets, title, times)
        if (fieldValid) {
            viewModelScope.launch {
                startLoad()
                delay(3000)
                finishLoad()
                val item = Training(sets.toInt(), title, times, rest, trainingId)
                editTrainingUseCase.editTraining(item)
                finishWork()
            }
        }
    }

    private fun parseSets(inputSets: String?) = inputSets?.trim() ?: ""

    private fun parseTitle(inputTitle: String?) = inputTitle?.trim() ?: ""

    private fun parseTimes(inputTimes: String?) = inputTimes?.trim() ?: ""

    private fun parseRest(inputRest: String?) = inputRest?.trim() ?: ""

    private fun validateInput(sets: String, title: String, times: String): Boolean {
        var res = true
        if (times.isBlank()) {
            _errorInputTimes.value = true
            res = false
        }
        if (title.isBlank()) {
            _errorInputTitle.value = true
            res = false
        }
        if (sets.isBlank()) {
            _errorInputSets.value = true
            res = false
        }
        return res
    }

    fun resetErrorInputSets() {
        _errorInputSets.value = false
    }

    fun resetErrorInputTitle() {
        _errorInputTitle.value = false
    }

    fun resetErrorInputTimes() {
        _errorInputTimes.value = false
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }

    private fun startLoad() {
        _loading.value = true
    }

    private fun finishLoad() {
        _loading.value = false
    }
}