package com.trainingtimer.views.calendar

import androidx.lifecycle.ViewModel
import com.trainingtimer.domain.CalendarDay
import com.trainingtimer.views.calendar.week.areDatesEqual
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
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
        val calendar = Calendar.getInstance()
        currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

        val weeks = mutableListOf<List<CalendarDay>>()
        val today = calendar.time

        // Генерация текущей недели и нескольких вокруг
        weeks.add(generateWeek(today))
        for (i in 1..5) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            weeks.add(0, generateWeek(calendar.time)) // Добавляем в начало

            calendar.add(Calendar.WEEK_OF_YEAR, 2)
            weeks.add(generateWeek(calendar.time)) // Добавляем в конец
        }

        for (j in 1..30) {
            calendar.add(Calendar.DATE, j)
            events.add(calendar.time)
        }

        _loadedWeeks.value = weeks
    }

    private fun generateWeek(date: Date): List<CalendarDay> {
        val week = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.time = date

        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        for (i in 0 until 7) {
            week.add(CalendarDay(calendar.get(Calendar.DAY_OF_MONTH).toString(), calendar.time))
            calendar.add(Calendar.DATE, 1)
        }

        return week
    }

    fun loadPreviousWeeks() {
        val firstWeekDate = _loadedWeeks.value.firstOrNull()?.firstOrNull()?.date
            ?: return // Если список пуст, ничего не делаем

        val calendar = Calendar.getInstance()
        calendar.time = firstWeekDate
        calendar.add(Calendar.WEEK_OF_YEAR, -1)

        val newWeeks = mutableListOf<List<CalendarDay>>()
        for (i in 0 until 5) { // Добавляем 5 новых недель
            newWeeks.add(0, generateWeek(calendar.time))
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
        }

        // Пересчитываем позицию текущей недели относительно новых данных
        currentWeekNumber += newWeeks.size
        _loadedWeeks.value = newWeeks + _loadedWeeks.value
    }

    fun loadNextWeeks() {
        val lastWeekDate = _loadedWeeks.value.lastOrNull()?.lastOrNull()?.date
            ?: return // Если список пуст, ничего не делаем

        val calendar = Calendar.getInstance()
        calendar.time = lastWeekDate
        calendar.add(Calendar.WEEK_OF_YEAR, 1)

        val newWeeks = mutableListOf<List<CalendarDay>>()
        for (i in 0 until 5) { // Добавляем 5 новых недель
            newWeeks.add(generateWeek(calendar.time))
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        _loadedWeeks.value = _loadedWeeks.value + newWeeks
    }

    fun getWeekPositionForDate(date: Date): Int {
        return _loadedWeeks.value.indexOfFirst { week ->
            week.any { areDatesEqual(it.date, date) }
        }.takeIf { it != -1 } ?: currentWeekNumber
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