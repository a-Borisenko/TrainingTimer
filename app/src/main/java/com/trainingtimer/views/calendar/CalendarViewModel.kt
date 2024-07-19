package com.trainingtimer.views.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.utils.DataService
import com.trainingtimer.views.list.TrainingUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewModel : ViewModel() {

//    private val getTrainingListUseCase = GetTrainingListUseCase(rep.getRep())
//    private val deleteTrainingUseCase = DeleteTrainingUseCase(rep.getRep())

//    val trainingList = getTrainingListUseCase.getTrainingList()

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private val _uiState = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    fun loadView() {
        viewModelScope.launch {
            if (DataService.needLoading) {
                _uiState.value = TrainingUiState.Loading
                delay(2000)
            }
            _uiState.value = TrainingUiState.Loaded
            DataService.needLoading = false
        }
    }

    /*fun deleteTraining(training: Training) {
        deleteTrainingUseCase.deleteTraining(training)
    }*/
}