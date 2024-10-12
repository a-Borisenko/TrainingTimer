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

    private val _loadedDates = MutableStateFlow<List<Date>>(emptyList())
    val loadedDates: StateFlow<List<Date>> get() = _loadedDates

    val events = mutableListOf<Date>()
    var latestPos = 0

    init {
        initializeDates()
    }

    private fun initializeDates() {
        val calendar = java.util.Calendar.getInstance()
        val initialDate = calendar.time
        _selectedMonthDate.value = initialDate

        // Инициализация событий на ближайшие 10 дней
        for (i in 1..10) {
            calendar.add(java.util.Calendar.DATE, i)
            events.add(calendar.time)
        }

        // Инициализация списка дат
        _loadedDates.value = generateDatesAround(initialDate)
        latestPos = _loadedDates.value.size / 2
    }

    private fun generateDatesAround(baseDate: Date): List<Date> {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = baseDate

        val dates = mutableListOf<Date>()
        val initialDate = calendar.time

        // Добавляем предыдущие 5 месяцев
        for (i in -5..-1) {
            calendar.add(java.util.Calendar.MONTH, i)
            dates.add(calendar.time)
            calendar.time = initialDate
        }

        // Добавляем текущий месяц
        dates.add(initialDate)

        // Добавляем следующие 5 месяцев
        calendar.time = initialDate
        for (i in 1..5) {
            calendar.add(java.util.Calendar.MONTH, i)
            dates.add(calendar.time)
            calendar.time = initialDate
        }

        return dates
    }

    fun dateFormatter(date: Date): String {
        val sdf = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun loadPreviousMonths() {
        val calendar = java.util.Calendar.getInstance()
        val currentLoadedDates = _loadedDates.value.toMutableList()

        // Работа с первой датой
        calendar.time = currentLoadedDates.first()

        // Добавляем предыдущие 12 месяцев
        val newDates = mutableListOf<Date>()
        for (i in 1..12) {
            calendar.add(java.util.Calendar.MONTH, -1)
            newDates.add(calendar.time)
        }

        // Объединяем новые даты с текущими и обновляем состояние
        _loadedDates.value = (newDates + currentLoadedDates).sortedBy { it.time }
        latestPos += 12
    }

    fun loadNextMonths() {
        val calendar = java.util.Calendar.getInstance()
        val currentLoadedDates = _loadedDates.value.toMutableList()

        // Работа с последней датой
        calendar.time = currentLoadedDates.last()

        // Добавляем следующие 12 месяцев
        val newDates = mutableListOf<Date>()
        for (i in 1..12) {
            calendar.add(java.util.Calendar.MONTH, 1)
            newDates.add(calendar.time)
        }

        // Объединяем новые даты с текущими и обновляем состояние
        _loadedDates.value = (currentLoadedDates + newDates).sortedBy { it.time }
    }

    fun updateSelectedDate(position: Int) {
        _selectedMonthDate.value = _loadedDates.value[position]
    }
}