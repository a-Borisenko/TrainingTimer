package com.trainingtimer.views.calendar.date

import com.trainingtimer.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    if (dateFirst == null || dateSecond == null) {
        return false
    }
    return sdf.format(dateFirst).equals(sdf.format(dateSecond))
}

class CalendarCellStyleProvider(
    private val events: List<Date>
) {
    fun getBackgroundResourceForCell(date: Date, currentMonth: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date

        return when {
            areDatesEqual(calendar.time, date) -> R.drawable.calendar_cell_today
            isEventDay(date) -> R.drawable.calendar_cell_event
            isInTheSelectedMonth(date, currentMonth) -> R.drawable.calendar_cell_background
            else -> R.drawable.calendar_cell_gray
        }
    }

    private fun isEventDay(date: Date): Boolean {
        return events.any { areDatesEqual(it, date) }
    }

    private fun isInTheSelectedMonth(dateFirst: Date, dateSecond: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        return sdf.format(dateFirst).equals(sdf.format(dateSecond))
    }
}