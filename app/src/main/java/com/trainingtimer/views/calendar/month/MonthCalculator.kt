package com.trainingtimer.views.calendar.month

import com.trainingtimer.domain.CalendarDay
import java.util.Calendar
import java.util.Date

class MonthCalculator {
    fun getDaysInMonth(date: Date): List<CalendarDay> {
        val daysInMonthList: MutableList<CalendarDay> = mutableListOf()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2

        when(dayOfWeek){
            -1 -> {
                calendar.add(Calendar.DAY_OF_MONTH,-6)
                while(daysInMonthList.size < 42){
                    daysInMonthList.add(CalendarDay(
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        calendar.time
                    ))
                    calendar.add(Calendar.DAY_OF_MONTH,1)
                }
            }
            else -> {
                calendar.add(Calendar.DAY_OF_MONTH,-dayOfWeek)
                while(daysInMonthList.size < 42){
                    daysInMonthList.add(CalendarDay(
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        calendar.time
                    ))
                    calendar.add(Calendar.DAY_OF_MONTH,1)
                }
            }
        }
        return daysInMonthList
    }
}