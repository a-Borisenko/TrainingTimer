package com.trainingtimer.presentation.details

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.domain.entity.Training
import com.trainingtimer.domain.usecases.AddTrainingUseCase
import com.trainingtimer.domain.usecases.EditTrainingUseCase
import com.trainingtimer.domain.usecases.GetTrainingListUseCase
import com.trainingtimer.domain.usecases.GetTrainingUseCase
import com.trainingtimer.presentation.details.TimerService.Companion.START
import com.trainingtimer.utils.timeLongToString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrainingViewModel @Inject constructor(
    getTrainingUseCase: GetTrainingUseCase,
    private val addTrainingUseCase: AddTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    private val getTrainingListUseCase: GetTrainingListUseCase,
    application: Application,
    private val trainingId: Int
) : ViewModel() {

    private var newId = 0

    private val _state = MutableStateFlow(TrainingState())
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            getTrainingListUseCase.getTrainingList().onEach { newId = it.last().id + 1 }
        }

        val isCounting = application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getBoolean("service_running", false)

        if (trainingId != Training.UNDEFINED_ID) {
            getTrainingUseCase.getTraining(trainingId)
                .filterNotNull()
                .onEach {
                    _state.update { currentState ->
                        currentState.copy(
                            sets = it.sets.toString(),
                            title = it.title,
                            times = it.times.drop(1),
                            secRemain = if (!state.value.isCounting) {
                                it.rest
                            } else {
                                currentState.secRemain
                            }
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
        resetProgress()

        if (isCounting) timerServiceObserve()
    }

    fun updateTime(sec: Long) {
        _state.update { it.copy(secRemain = timeLongToString(sec)) }
        resetProgress()
    }

    private fun resetProgress() {
        _state.update { it.copy(progress = if (trainingId != Training.UNDEFINED_ID) 100 else 0) }
    }

    fun startTimer(time: String, appContext: Context) {
        _state.update { it.copy(isCounting = true) }

        ContextCompat.startForegroundService(
            appContext,
            TimerService.newIntent(appContext, START, timeStringToLong(time))
        )
        timerServiceObserve()
    }

    private fun timerServiceObserve() {
        TimerService.timerStateFlow.onEach { timerState ->
            _state.update {
                it.copy(
                    secRemain = timeLongToString(timerState.secRemain),
                    progress = timerState.progress.toInt()
                )
            }
        }.launchIn(viewModelScope)
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
                if (trainingId == Training.UNDEFINED_ID) {
                    val item = Training(sets.toInt(), title, "x$reps", time, newId)
                    addTrainingUseCase.addTraining(item)
                } else {
                    val item = Training(sets.toInt(), title, "x$reps", time, trainingId)
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


    private fun timeStringToLong(time: String): Long {
        val min = (time.split(":"))[0].toLong()
        val sec = (time.split(":"))[1].toLong()
        return (min * 60 + sec)
    }
}