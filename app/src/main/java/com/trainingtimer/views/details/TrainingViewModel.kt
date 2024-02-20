package com.trainingtimer.views.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.data.TrainingRepositoryImpl
import com.trainingtimer.domain.AddTrainingUseCase
import com.trainingtimer.domain.EditTrainingUseCase
import com.trainingtimer.domain.GetTrainingListUseCase
import com.trainingtimer.domain.GetTrainingUseCase
import com.trainingtimer.domain.Training
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val timerService: TimerService
) : ViewModel() {

    private val repository = TrainingRepositoryImpl.get()
    private val getTrainingUseCase = GetTrainingUseCase(repository)
    private val addTrainingUseCase = AddTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)
    private val getTrainingListUseCase = GetTrainingListUseCase(repository)

    private var saveState = false
    private var startProgress = 0F
    private var newId = 0

    private val _errorInputSets = MutableStateFlow(false)
    val errorInputSets: StateFlow<Boolean> = _errorInputSets.asStateFlow()

    private val _errorInputTitle = MutableStateFlow(false)
    val errorInputTitle: StateFlow<Boolean> = _errorInputTitle.asStateFlow()

    private val _errorInputTimes = MutableStateFlow(false)
    val errorInputTimes: StateFlow<Boolean> = _errorInputTimes.asStateFlow()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    private val _secRemain = MutableStateFlow(TimerService.timerInitValue)
    val secRemain: StateFlow<Long> = _secRemain.asStateFlow()

    /*val secRem: SharedFlow<Long> = flow {
        emit(100L)
    }.shareIn(viewModelScope, started = SharingStarted.Lazily, replay = 1)*/

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
            if (!TimerService.isCounting) {
                _secRemain.value = timeStringToLong(it.rest)
            }
        }
    }

    /*private val serviceTime = Observer<Long> {
        _secRemain.value = it
    }*/

    /*private val serviceProgress = Observer<Float> {
        _progress.value = it
    }*/

    private val trainingsNumber = Observer<List<Training>> {
        newId = it.last().id + 1
    }


    init {
        viewModelScope.launch {
            TimerService.secRemainFlow
                .collect {
                    _secRemain.value = it
                }
        }
        viewModelScope.launch {
            TimerService.progressFlow
                .collect {
                    _progress.value = it
                }
        }
    }

    fun start(id: Int) {
//        TimerService.secRemainLD.observeForever(serviceTime)
//        TimerService.progressLD.observeForever(serviceProgress)
        getTrainingListUseCase.getTrainingList().observeForever(trainingsNumber)


        if (_secRemain.value == 0L) {
            _progress.value = 0f
        } /*else {
            resetProgress()
        }*/

        launchMode(id)
    }

    override fun onCleared() {
        super.onCleared()
//        TimerService.secRemainLD.removeObserver(serviceTime)
//        TimerService.progressLD.removeObserver(serviceProgress)
    }

    fun saveState(sets: String, title: String, times: String) {
        saveState = true
        _sets.value = sets
        _title.value = title
        _times.value = times
    }

    fun updateTime(sec: Long) {
        _secRemain.value = sec
    }

    fun resetProgress(id: Int) {
        if (id != Training.UNDEFINED_ID) {
            _progress.value = 100f
        } else {
            _progress.value = 0f
        }
        Log.d("viewModel", "progress ${_progress.value}")
    }

    private fun launchMode(id: Int) {
        if (!TimerService.isCounting) {
            resetProgress(id)
        }
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

    fun trainingClickData(
        inputSets: String?,
        inputTitle: String?,
        inputReps: String?,
        inputTime: String?,
        trainingId: Int
    ) {
        val sets = parseSets(inputSets)
        val title = parseTitle(inputTitle)
        val reps = parseTimes(inputReps)
        val time = parseRest(inputTime)
        val fieldValid = validateInput(sets, title, reps)
        if (fieldValid) {
            viewModelScope.launch {
                startLoad()
                delay(3000)
                finishLoad()
                if (trainingId == Training.UNDEFINED_ID) {
                    val item = Training(sets.toInt(), title, "x$reps", time, newId)
                    addTrainingUseCase.addTraining(item)
                } else {
                    val item = Training(sets.toInt(), title, "x$reps", time, trainingId)
                    editTrainingUseCase.editTraining(item)
                }
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