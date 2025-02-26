package com.trainingtimer.presentation.calendar

import com.trainingtimer.domain.entity.Day
import java.util.Date

class CalendarState(
    val weeksList: List<List<Day>>,
    val selectedWeekDate: Date
) {
}