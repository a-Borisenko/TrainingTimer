package com.trainingtimer.views.calendar.date

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.CalendarDay

class DayViewHolder(
    private val binding: CalendarCellBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(calendarDay: CalendarDay) {
        binding.textView.text = calendarDay.dayOfMonth
        // Настройка отображения событий, выделения выбранных дней и т.д.
    }
}