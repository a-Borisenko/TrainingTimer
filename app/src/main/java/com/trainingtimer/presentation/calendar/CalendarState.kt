package com.trainingtimer.presentation.calendar

import com.trainingtimer.domain.entity.Day
import java.util.Date

sealed class CalendarState(
    val selectedWeekDate: Date,
    val loadedWeeks: List<List<Day>>
)
