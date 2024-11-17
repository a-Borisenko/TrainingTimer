package com.trainingtimer.views.calendar

import androidx.lifecycle.ViewModel
import com.trainingtimer.domain.CalendarDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarViewModel : ViewModel() {

    private val _selectedWeekDate = MutableStateFlow(Date())
    val selectedWeekDate: StateFlow<Date> get() = _selectedWeekDate

    private val _loadedWeeks = MutableStateFlow<List<List<CalendarDay>>>(emptyList())
    val loadedWeeks: StateFlow<List<List<CalendarDay>>> get() = _loadedWeeks

    val events = mutableListOf<Date>()
    var latestPos: Int = 0
    var currentWeekNumber = 0

    init {
        initializeWeeks()
    }

    private fun initializeWeeks() {
        val calendar = java.util.Calendar.getInstance()
        currentWeekNumber = calendar.get(java.util.Calendar.WEEK_OF_YEAR)

        calendar.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY)
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)

        val weeks = mutableListOf<List<CalendarDay>>()
        for (i in 0 until 52) {
            weeks.add(generateWeek(calendar.time))
            if (calendar.get(java.util.Calendar.MONTH) == java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)) {
                latestPos = i
            }
            calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
        }

        for (j in 1..30) {
            calendar.add(java.util.Calendar.DATE, j)
            events.add(calendar.time)
        }

        _loadedWeeks.value = weeks
    }

    private fun generateWeek(date: Date): List<CalendarDay> {
        val week = mutableListOf<CalendarDay>()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date

        calendar.firstDayOfWeek = java.util.Calendar.MONDAY
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        for (i in 0 until 7) {
            week.add(CalendarDay(calendar.get(java.util.Calendar.DAY_OF_MONTH).toString(), calendar.time))
            calendar.add(java.util.Calendar.DATE, 1)
        }

        return week
    }

    fun dateFormatter(date: Date): String {
        val sdf = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun updateSelectedWeek(position: Int) {
        if (position in _loadedWeeks.value.indices) {
            _selectedWeekDate.value = _loadedWeeks.value[position].first().date
        }
    }
}