package com.trainingtimer.views.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarViewModel : ViewModel() {

    private val _selectedMonthDate = MutableStateFlow(Date())
    val selectedMonthDate: StateFlow<Date> get() = _selectedMonthDate


    private val _loadedDates = MutableStateFlow<MutableList<Date>>(mutableListOf())
    val loadedDates: StateFlow<MutableList<Date>> get() = _loadedDates


    val events = mutableListOf<Date>()
    var latestPos = 0

    init {
        initializeDates()
    }

    private fun initializeDates() {
        val calendar = java.util.Calendar.getInstance()
        val initialDate = calendar.time
        _selectedMonthDate.value = initialDate

        for (i in 1..10) {
            calendar.add(java.util.Calendar.DATE, i)
            events.add(calendar.time)
        }

        val loadedDates = mutableListOf<Date>()

        for (i in -5..-1) {
            calendar.add(java.util.Calendar.MONTH, i)
            loadedDates.add(calendar.time)
            calendar.time = initialDate
        }
        loadedDates.add(initialDate)
        calendar.time = initialDate
        for (i in 1..5) {
            calendar.add(java.util.Calendar.MONTH, i)
            loadedDates.add(calendar.time)
            calendar.time = initialDate
        }

        _loadedDates.value = loadedDates
        latestPos = loadedDates.size / 2
    }

    fun dateFormatter(date: Date): String {
        val sdf = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun loadPreviousMonths() {
        val calendar = java.util.Calendar.getInstance()
        val loadedDates = _loadedDates.value
        calendar.time = loadedDates[0]

        for (i in 1..12) {
            calendar.add(java.util.Calendar.MONTH, -1)
            loadedDates.add(calendar.time)
        }
        latestPos += 12
        loadedDates.sort()
        _loadedDates.value = loadedDates
    }

    fun loadNextMonths() {
        val calendar = java.util.Calendar.getInstance()
        val loadedDates = _loadedDates.value
        calendar.time = loadedDates.last()

        for (i in 1..12) {
            calendar.add(java.util.Calendar.MONTH, 1)
            loadedDates.add(calendar.time)
        }
        loadedDates.sort()
        _loadedDates.value = loadedDates
    }

    fun updateSelectedDate(position: Int) {
        val loadedDates = _loadedDates.value
        _selectedMonthDate.value = loadedDates[position]
    }
}