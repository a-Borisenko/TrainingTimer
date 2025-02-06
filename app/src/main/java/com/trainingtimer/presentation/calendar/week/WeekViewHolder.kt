package com.trainingtimer.presentation.calendar.week

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.WeekItemBinding
import com.trainingtimer.domain.entity.Day
import com.trainingtimer.utils.areDatesEqual
import com.trainingtimer.utils.dayOfMonth
import com.trainingtimer.utils.dayOfWeek
import java.util.Calendar
import java.util.Date

class WeekViewHolder(
    val binding: WeekItemBinding,
    private val events: List<Date>,
    val onClick: (week: Int, day: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val weekDays = listOf(
        binding.mon, binding.tue, binding.wed, binding.thu, binding.fri, binding.sat, binding.sun
    )

    fun bind(week: List<Day>, position: Int) {
        week.forEachIndexed { index, day ->
            val dateView = weekDays[index]
            dateView.text = dayOfMonth(day.date)
            dateView.setOnClickListener {
                onClick(position, dayOfWeek(index))
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