package com.trainingtimer.views.calendar.month

import com.trainingtimer.domain.CalendarDay
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

    fun getDaysInWeek(date: Date): List<CalendarDay> {
        val daysInWeekList: MutableList<CalendarDay> = mutableListOf()
        val calendar = Calendar.getInstance()

        calendar.time = date
        // Найти первый день недели (понедельник)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0 until 7) { // Генерация 7 дней
            daysInWeekList.add(
                CalendarDay(
                    calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    calendar.time
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return daysInWeekList
    }

    fun getWeeksInMonth(date: Date): List<List<CalendarDay>> {
        val weeksInMonthList: MutableList<List<CalendarDay>> = mutableListOf()
        val calendar = Calendar.getInstance()

        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        calendar.add(Calendar.DAY_OF_MONTH, -dayOfWeek)

        // Генерация недель
        while (weeksInMonthList.size < 6) {  // В месяц максимум 6 недель
            val week: MutableList<CalendarDay> = mutableListOf()
            for (i in 0 until 7) {
                week.add(
                    CalendarDay(
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        calendar.time
                    )
                )
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            weeksInMonthList.add(week)
        }

        return weeksInMonthList
    }
}