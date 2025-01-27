package com.trainingtimer.views.calendar.week

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.WeekItemBinding
import com.trainingtimer.domain.Day
import com.trainingtimer.utils.areDatesEqual
import com.trainingtimer.utils.dayOfMonth
import java.util.Calendar
import java.util.Date

class WeekViewHolder(
    val binding: WeekItemBinding,
    private val events: List<Date>
) : RecyclerView.ViewHolder(binding.root) {

    private val weekDays = listOf(
        binding.mon, binding.tue, binding.wed, binding.thu, binding.fri, binding.sat, binding.sun
    )

    var onClick: ((week: Int, day: Int) -> Unit)? = null

    fun bind(week: List<Day>, position: Int) {
        week.forEachIndexed { index, day ->
            val dateView = weekDays[index]
            dateView.text = dayOfMonth(day.date)
            dateView.setOnClickListener {
                onClick?.invoke(position, index)
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