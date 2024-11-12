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
    var latestPos = 0

    init {
        initializeWeeks()
    }

    private fun initializeWeeks() {
        val calendar = java.util.Calendar.getInstance()
        val initialDate = calendar.time
        _selectedWeekDate.value = initialDate

        // Добавляем события на 10 дней для тестирования
        for (i in 1..10) {
            calendar.add(java.util.Calendar.DATE, i)
            events.add(calendar.time)
        }

        _loadedWeeks.value = generateWeeksAround(initialDate)
        latestPos = _loadedWeeks.value.size / 2
    }

    private fun generateWeeksAround(baseDate: Date): List<List<CalendarDay>> {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = baseDate

        val weeks = mutableListOf<List<CalendarDay>>()

        // Добавляем недели перед базовой неделей
        for (i in -5..-1) {
            weeks.add(generateWeek(calendar.apply { add(java.util.Calendar.WEEK_OF_YEAR, i) }.time))
        }

        // Базовая неделя
        weeks.add(generateWeek(baseDate))

        // Добавляем недели после базовой недели
        for (i in 1..5) {
            weeks.add(generateWeek(calendar.apply { add(java.util.Calendar.WEEK_OF_YEAR, i) }.time))
        }

        return weeks
    }

    private fun generateWeek(date: Date): List<CalendarDay> {
        val week = mutableListOf<CalendarDay>()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date

        // Перематываем к началу недели
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        // Генерируем 7 дней для недели
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

    fun loadPreviousWeeks() {
        val currentLoadedWeeks = _loadedWeeks.value.toMutableList()
        val firstWeek = currentLoadedWeeks.first().firstOrNull()?.date ?: return

        val newWeeks = mutableListOf<List<CalendarDay>>()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = firstWeek

        for (i in 1..6) {
            calendar.add(java.util.Calendar.WEEK_OF_YEAR, -1)
            newWeeks.add(0, generateWeek(calendar.time))
        }

        _loadedWeeks.value = newWeeks + currentLoadedWeeks
        latestPos += newWeeks.size
    }

    fun loadNextWeeks() {
        val currentLoadedWeeks = _loadedWeeks.value.toMutableList()
        val lastWeek = currentLoadedWeeks.last().lastOrNull()?.date ?: return

        val newWeeks = mutableListOf<List<CalendarDay>>()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = lastWeek

        for (i in 1..6) {
            calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
            newWeeks.add(generateWeek(calendar.time))
        }

        _loadedWeeks.value = currentLoadedWeeks + newWeeks
    }

    fun updateSelectedWeek(position: Int) {
        // Проверяем, что индекс позиции находится в пределах загруженных недель
        if (position in _loadedWeeks.value.indices) {
            _selectedWeekDate.value = _loadedWeeks.value[position].first().date
        }
    }
}