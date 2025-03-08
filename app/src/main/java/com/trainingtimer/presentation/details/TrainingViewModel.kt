package com.trainingtimer.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.domain.entity.Training
import com.trainingtimer.domain.usecases.AddTrainingUseCase
import com.trainingtimer.domain.usecases.EditTrainingUseCase
import com.trainingtimer.domain.usecases.GetTrainingListUseCase
import com.trainingtimer.domain.usecases.GetTrainingUseCase
import com.trainingtimer.utils.DataService
import com.trainingtimer.utils.timeStringToLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class TrainingViewModel @Inject constructor(
    getTrainingUseCase: GetTrainingUseCase,
    private val addTrainingUseCase: AddTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    getTrainingListUseCase: GetTrainingListUseCase
) : ViewModel() {

    var currentId: Int by Delegates.observable(Training.UNDEFINED_ID) { _, _, _ ->
        if (currentId != Training.UNDEFINED_ID) {
            getTrainingUseCase.getTraining(currentId)
                .filterNotNull()
                .onEach {
                    _state.update { currentState ->
                        currentState.copy(
                            sets = it.sets.toString(),
                            title = it.title,
                            times = it.times.drop(1),
                            secRemain = if (!DataService.isCounting) {
                                timeStringToLong(it.rest)
                            } else {
                                currentState.secRemain
                            }
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
        resetProgress()
    }
    private var newId = 0

    private val _state = MutableStateFlow(TrainingState())
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            getTrainingListUseCase.getTrainingList().onEach { newId = it.last().id + 1 }
        }

        TimerService.secRemainFlow.onEach { secRemain ->
            _state.update { it.copy(secRemain = secRemain) }
        }.launchIn(viewModelScope)

        TimerService.progressFlow.onEach { progress ->
            _state.update { it.copy(progress = progress) }
        }.launchIn(viewModelScope)

        TimerService.isLast = false
    }

    fun updateTime(sec: Long) {
        _state.update { it.copy(secRemain = sec) }
        resetProgress()
    }

    private fun resetProgress() {
        _state.update { it.copy(progress = if (currentId != Training.UNDEFINED_ID) 100f else 0f) }
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
                if (currentId == Training.UNDEFINED_ID) {
                    val item = Training(sets.toInt(), title, "x$reps", time, newId)
                    addTrainingUseCase.addTraining(item)
                } else {
                    val item = Training(sets.toInt(), title, "x$reps", time, currentId)
                    editTrainingUseCase.editTraining(item)
                }
                _state.update { it.copy(shouldCloseScreen = true) }
            }
        }
    }

    private fun parseInput(input: String?) = input?.trim() ?: ""

    private fun validateInput(sets: String, title: String, times: String): Boolean {
        val setsValid = sets.isNotBlank()
        _state.update { it.copy(errorInputSets = !setsValid) }

        val titleValid = title.isNotBlank()
        _state.update { it.copy(errorInputTitle = !titleValid) }

        val timesValid = times.isNotBlank()
        _state.update { it.copy(errorInputTimes = !timesValid) }

        return setsValid && titleValid && timesValid
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