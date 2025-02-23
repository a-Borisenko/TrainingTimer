package com.trainingtimer.presentation.calendar.week

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.WeekItemBinding
import com.trainingtimer.domain.entity.Day
import com.trainingtimer.utils.areDatesEqual
import com.trainingtimer.utils.dayOfMonth
import com.trainingtimer.utils.dayOfWeek
import com.trainingtimer.utils.weekNumber
import java.util.Calendar
import java.util.Date

class WeekViewHolder(
    binding: WeekItemBinding,
    private val events: List<Date>,
    val onClick: (week: String, day: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val weekDays = listOf(
        binding.mon, binding.tue, binding.wed, binding.thu, binding.fri, binding.sat, binding.sun
    )

    fun bind(week: List<Day>) {
        week.forEachIndexed { index, day ->
            val dateView = weekDays[index]
            dateView.text = dayOfMonth(day.date)
            dateView.setOnClickListener {
                onClick(weekNumber(week[index].date), dayOfWeek(index))
            }

            val isToday = areDatesEqual(Calendar.getInstance().time, day.date)
            val isEventDay = events.any { areDatesEqual(it, day.date) }

            when {
                isToday -> dateView.setBackgroundResource(R.drawable.calendar_cell_today)
                isEventDay -> dateView.setBackgroundResource(R.drawable.calendar_cell_event)
                else -> dateView.setBackgroundResource(R.drawable.calendar_cell_background)
            }
        }
    }
}