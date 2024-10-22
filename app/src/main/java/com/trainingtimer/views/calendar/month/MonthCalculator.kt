package com.trainingtimer.views.calendar.month

import com.trainingtimer.domain.CalendarDay
import com.trainingtimer.views.calendar.week.Week
import java.util.Calendar
import java.util.Date

class MonthCalculator {

    fun getDaysInMonth(date: Date): List<CalendarDay> {
        val daysInMonthList: MutableList<CalendarDay> = mutableListOf()
        val calendar = Calendar.getInstance()

        // Установить календарь на начало месяца
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Определяем день недели, с которого начинается месяц
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1  // Sunday == 1, поэтому делаем сдвиг
        calendar.add(Calendar.DAY_OF_MONTH, -dayOfWeek)  // Сдвиг на начало предыдущей недели

        // Заполняем 42 дня (6 недель)
        while (daysInMonthList.size < 42) {
            daysInMonthList.add(
                CalendarDay(
                    calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    calendar.time
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)  // Переходим к следующему дню
        }

        return daysInMonthList
    }

    fun getWeeksInMonth(date: Date): List<Week> {
        val daysInMonth = getDaysInMonth(date)
        val weeks = mutableListOf<Week>()

        // Группируем дни по неделям (по 7 дней)
        for (i in 0 until daysInMonth.size step 7) {
            weeks.add(Week(daysInMonth.subList(i, i + 7)))
        }

        return weeks
    }
}