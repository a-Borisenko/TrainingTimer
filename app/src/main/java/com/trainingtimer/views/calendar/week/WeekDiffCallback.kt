package com.trainingtimer.views.calendar.week

import androidx.recyclerview.widget.DiffUtil
import com.trainingtimer.domain.CalendarDay

class WeekDiffCallback : DiffUtil.ItemCallback<List<CalendarDay>>() {

    override fun areItemsTheSame(oldItem: List<CalendarDay>, newItem: List<CalendarDay>): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: List<CalendarDay>, newItem: List<CalendarDay>): Boolean {
        return oldItem == newItem
    }
}