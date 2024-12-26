package com.trainingtimer.views.calendar

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.trainingtimer.domain.CalendarDay
import com.trainingtimer.domain.RecyclerHeightProvider
import com.trainingtimer.utils.areDatesEqual
import com.trainingtimer.views.calendar.week.WeekAdapter
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

    private val events = mutableListOf<Date>()

    var latestPos: Int = 0
    var currentWeekNumber = 0
    lateinit var adapter: WeekAdapter

    init {
        initializeWeeks()
    }

    private fun initializeWeeks() {
        val calendar = Calendar.getInstance()

        val weeks = mutableListOf<List<CalendarDay>>()
        weeks.add(generateWeek(calendar.time))

        for (i in 1..5) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            weeks.add(0, generateWeek(calendar.time))
        }

        calendar.add(Calendar.WEEK_OF_YEAR, 5)

        for (i in 1..5) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            weeks.add(generateWeek(calendar.time))
        }

        for (j in 1..30) {
            calendar.add(Calendar.DATE, j)
            events.add(calendar.time)
        }

        _loadedWeeks.value = weeks

        _loadedWeeks.value.forEachIndexed { index, week ->
            Log.d("CalendarViewModel", "Week $index: ${week.first().date} - ${week.last().date}")
        }
        loadPreviousWeeks()
    }

    fun setupAdapter(
        context: Context,
        recyclerHeightProvider: RecyclerHeightProvider,
        onDateSelected: (Date?) -> Unit
    ): WeekAdapter {
        val sdf = SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault())
        adapter = WeekAdapter(context, events, recyclerHeightProvider) { selectedDate ->
            val dateMessage = selectedDate?.let {
                "Selected date is: ${sdf.format(it)}"
            } ?: "Selected date is: no data"

            onDateSelected(selectedDate)
            Log.d("CalendarViewModel", dateMessage)
        }
        return adapter
    }

    private fun generateWeek(date: Date): List<CalendarDay> {
        val week = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        calendar.time = date

        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0 until 7) {
            week.add(CalendarDay(calendar.get(Calendar.DAY_OF_MONTH).toString(), calendar.time))
            calendar.add(Calendar.DATE, 1)
            Log.d("generateWeek", "Generated week: ${week.first().date} to ${week.last().date}")
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
        val lastWeekDate = _loadedWeeks.value.lastOrNull()?.lastOrNull()?.date ?: return
        Log.d("loadNextWeeks", "Last week date: $lastWeekDate")

        val calendar = Calendar.getInstance()
        calendar.time = lastWeekDate

        val newWeeks = mutableListOf<List<CalendarDay>>()
        for (i in 0 until 5) {
            val week = generateWeek(calendar.time)
            newWeeks.add(week)
            Log.d("loadNextWeeks", "Generated week: ${week.first().date} to ${week.last().date}")
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        val updatedWeeks = _loadedWeeks.value + newWeeks
        _loadedWeeks.value = updatedWeeks
        Log.d("loadNextWeeks", "Updated weeks size: ${_loadedWeeks.value.size}")
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
            latestPos = position
        }
    }
}