package com.trainingtimer.views.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarViewModel : ViewModel() {

    // Хранение текущей выбранной даты через StateFlow
    private val _selectedMonthDate = MutableStateFlow(Date())
    val selectedMonthDate: StateFlow<Date> get() = _selectedMonthDate

    val events = mutableListOf<Date>()

    // Хранение загруженных дат через StateFlow
    private val _loadedDates = MutableStateFlow<MutableList<Date>>(mutableListOf())
    val loadedDates: StateFlow<MutableList<Date>> get() = _loadedDates

    var latestPos = 0

    init {
        // Инициализация данных при запуске
        initializeDates()
    }

    // Функция для инициализации списка с датами
    private fun initializeDates() {
        val calendar = java.util.Calendar.getInstance()
        val initialDate = calendar.time
        _selectedMonthDate.value = initialDate

        // Создание списка произвольных событий
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

    // Форматирование даты
    fun dateFormatter(date: Date): String {
        val sdf = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    // Загрузка предыдущих месяцев
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

    // Загрузка следующих месяцев
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

    // Обновление даты
    fun updateSelectedDate(position: Int) {
        val loadedDates = _loadedDates.value
        _selectedMonthDate.value = loadedDates[position]
    }
}