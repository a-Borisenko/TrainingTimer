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

        val weeks = mutableListOf<List<CalendarDay>>()
        weeks.add(generateWeek(calendar.time)) // Текущая неделя

        // Добавляем 5 предыдущих недель
        for (i in 1..5) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            weeks.add(0, generateWeek(calendar.time)) // Вставляем в начало
        }

        // Возвращаемся к текущей дате
        calendar.add(Calendar.WEEK_OF_YEAR, 5)

        // Добавляем 5 будущих недель
        for (i in 1..5) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            weeks.add(generateWeek(calendar.time))
        }

        /*for (j in 1..30) {
            calendar.add(Calendar.DATE, j)
            events.add(calendar.time)
        }*/

        _loadedWeeks.value = weeks
    }

    private fun generateWeek(date: Date): List<CalendarDay> {
        val week = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Устанавливаем начало недели на понедельник
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0 until 7) {
            week.add(CalendarDay(calendar.get(Calendar.DAY_OF_MONTH).toString(), calendar.time))
            calendar.add(Calendar.DATE, 1) // Переход к следующему дню
        }

        return week
    }

    fun loadPreviousWeeks() {
        val firstWeekDate = _loadedWeeks.value.firstOrNull()?.firstOrNull()?.date
            ?: return

        val calendar = Calendar.getInstance()
        calendar.time = firstWeekDate
        calendar.add(Calendar.WEEK_OF_YEAR, -1)

        val newWeeks = mutableListOf<List<CalendarDay>>()
        for (i in 0 until 5) {
            newWeeks.add(0, generateWeek(calendar.time))
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
        }

        currentWeekNumber += newWeeks.size
        _loadedWeeks.value = newWeeks + _loadedWeeks.value
    }

    fun loadNextWeeks() {
        // Берем последнюю дату в загруженных неделях
        val lastWeekDate = _loadedWeeks.value.lastOrNull()?.lastOrNull()?.date
            ?: return // Если список пуст, ничего не делаем

        val calendar = Calendar.getInstance()
        calendar.time = lastWeekDate

        val newWeeks = mutableListOf<List<CalendarDay>>()
        for (i in 1..5) { // Добавляем 5 новых недель
            calendar.add(Calendar.WEEK_OF_YEAR, 1) // Переход к следующей неделе
            newWeeks.add(generateWeek(calendar.time))
        }

        // Обновляем список недель
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