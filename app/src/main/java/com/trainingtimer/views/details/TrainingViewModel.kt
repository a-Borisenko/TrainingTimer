package com.trainingtimer.views.details

import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.domain.AddTrainingUseCase
import com.trainingtimer.domain.EditTrainingUseCase
import com.trainingtimer.domain.GetTrainingListUseCase
import com.trainingtimer.domain.GetTrainingUseCase
import com.trainingtimer.domain.Training
import com.trainingtimer.utils.DataService
import com.trainingtimer.utils.timeStringToLong
import com.trainingtimer.utils.validateField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val addTrainingUseCase: AddTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    private val getTrainingListUseCase: GetTrainingListUseCase
) : ViewModel() {

    private var saveState = false
    private var startProgress = 0f
    private var newId = 0

    private val _errorInputSets = MutableStateFlow(false)
    val errorInputSets: StateFlow<Boolean> = _errorInputSets.asStateFlow()

    private val _errorInputTitle = MutableStateFlow(false)
    val errorInputTitle: StateFlow<Boolean> = _errorInputTitle.asStateFlow()

    private val _errorInputTimes = MutableStateFlow(false)
    val errorInputTimes: StateFlow<Boolean> = _errorInputTimes.asStateFlow()

    private val _shouldCloseScreen = MutableStateFlow<Unit?>(null)
    val shouldCloseScreen: StateFlow<Unit?> = _shouldCloseScreen.asStateFlow()

    private val _secRemain = MutableStateFlow(TimerService.secInit)
    val secRemain: StateFlow<Long> = _secRemain.asStateFlow()

    private val _progress = MutableStateFlow(startProgress)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _sets = MutableStateFlow("")
    val sets: StateFlow<String> = _sets.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _times = MutableStateFlow("")
    val times: StateFlow<String> = _times.asStateFlow()



    private val trainingData = Observer<Training?> {
        if (!saveState) {
            _sets.value = it.sets.toString()
            _title.value = it.title
            _times.value = it.times.drop(1)
            if (!DataService.isCounting) {
                _secRemain.value = timeStringToLong(it.rest)
            }
        }
    }

    private val trainingsNumber = Observer<List<Training>> {
        newId = it.last().id + 1
    }


    init {
        TimerService.secRemainFlow.onEach { _secRemain.value = it }.launchIn(viewModelScope)
        TimerService.progressFlow.onEach { _progress.value = it }.launchIn(viewModelScope)
        TimerService.isLast = false
    }

    fun startViewModel() {
        getTrainingListUseCase.getTrainingList().observeForever(trainingsNumber)

        if (DataService.currentId != Training.UNDEFINED_ID) {
            getTrainingUseCase.getTraining(DataService.currentId).observeForever(trainingData)
        }
        resetProgress()
    }

    fun saveState(sets: String, title: String, times: String) {
        saveState = true
        _sets.value = sets
        _title.value = title
        _times.value = times
    }

    fun updateTime(sec: Long) {
        _secRemain.value = sec
        resetProgress()
    }

    private fun resetProgress() {
        if (DataService.currentId != Training.UNDEFINED_ID) {
            _progress.value = 100f
        } else {
            _progress.value = 0f
        }
        Log.d("viewModel", "progress ${_progress.value}")
    }

    fun startTimer(time: Long) {
        if (!DataService.isCounting && time > 0L) {
            DataService.startTime = time
            TimerService.isLast = false
        }
    }

    override fun onCleared() {
        if (DataService.isCounting) {
            TimerService.isLast = true
        }
        super.onCleared()
    }

    fun trainingClickData(
        inputSets: String?,
        inputTitle: String?,
        inputReps: String?,
        inputTime: String?
    ) {
        val sets = parseInput(inputSets)
        val title = parseInput(inputTitle)
        val reps = parseInput(inputReps)
        val time = parseInput(inputTime)
        val fieldValid = validateInput(sets, title, reps)
        if (fieldValid) {
            viewModelScope.launch {
                DataService.needLoading = true
                if (DataService.currentId == Training.UNDEFINED_ID) {
                    val item = Training(sets.toInt(), title, "x$reps", time, newId)
                    addTrainingUseCase.addTraining(item)
                } else {
                    val item = Training(sets.toInt(), title, "x$reps", time, DataService.currentId)
                    editTrainingUseCase.editTraining(item)
                }
                finishWork()
            }
        }
    }

    private fun parseInput(input: String?) = input?.trim() ?: ""

    private fun validateInput(sets: String, title: String, times: String): Boolean {
        return validateField(sets, _errorInputSets) &&
                validateField(title, _errorInputTitle) &&
                validateField(times, _errorInputTimes)
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
}