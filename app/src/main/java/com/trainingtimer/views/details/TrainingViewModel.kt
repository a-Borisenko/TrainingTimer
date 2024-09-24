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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    getTrainingUseCase: GetTrainingUseCase,
    private val addTrainingUseCase: AddTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    getTrainingListUseCase: GetTrainingListUseCase
) : ViewModel() {

    private var saveState = false
    private var newId = 0

    private val _state = MutableStateFlow(TrainingState())
    val state: StateFlow<TrainingState> = _state.asStateFlow()


    private val trainingData = Observer<Training?> { training ->
        if (!saveState && training != null) {
            _state.update { currentState ->
                currentState.copy(
                    sets = training.sets.toString(),
                    title = training.title,
                    times = training.times.drop(1),
                    secRemain = if (!DataService.isCounting) {
                        timeStringToLong(training.rest)
                    } else {
                        currentState.secRemain
                    }
                )
            }
        }
    }

    private val trainingsNumber = Observer<List<Training>> {
        newId = it.last().id + 1
    }


    init {
        TimerService.secRemainFlow.onEach { secRemain ->
            _state.update { currentState ->
                currentState.copy(secRemain = secRemain)
            }
        }.launchIn(viewModelScope)

        TimerService.progressFlow.onEach { progress ->
            _state.update { currentState ->
                currentState.copy(progress = progress)
            }
        }.launchIn(viewModelScope)

        TimerService.isLast = false
        getTrainingListUseCase.getTrainingList().observeForever(trainingsNumber)

        if (DataService.currentId != Training.UNDEFINED_ID) {
            getTrainingUseCase.getTraining(DataService.currentId).observeForever(trainingData)
        }
        resetProgress()
    }

    fun updateTime(sec: Long) {
        _state.update { currentState ->
            currentState.copy(secRemain = sec)
        }
        resetProgress()
    }

    private fun resetProgress() {
        _state.update { currentState ->
            currentState.copy(
                progress = if (DataService.currentId != Training.UNDEFINED_ID) 100f else 0f
            )
        }
        Log.d("viewModel", "progress ${_state.value.progress}")
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
                _state.update { it.copy(shouldCloseScreen = true) }
            }
        }
    }

    private fun parseInput(input: String?) = input?.trim() ?: ""

    private fun validateInput(sets: String, title: String, times: String): Boolean {
        val s = if (sets.isBlank()) {
            _state.update { it.copy(errorInputSets = true) }
            false
        } else {
            _state.update { it.copy(errorInputSets = false) }
            true
        }

        val t = if (title.isBlank()) {
            _state.update { it.copy(errorInputTitle = true) }
            false
        } else {
            _state.update { it.copy(errorInputTitle = false) }
            true
        }

        val r = if (times.isBlank()) {
            _state.update { it.copy(errorInputTimes = true) }
            false
        } else {
            _state.update { it.copy(errorInputTimes = false) }
            true
        }

        return  s && t && r
    }

    fun resetErrorInputSets(sets: String) {
        _state.update { it.copy(sets = sets, errorInputSets = false) }
    }

    fun resetErrorInputTitle(title: String) {
        _state.update { it.copy(title = title, errorInputTitle = false) }
    }

    fun resetErrorInputTimes(times: String) {
        _state.update { it.copy(times = times, errorInputTimes = false) }
    }
}