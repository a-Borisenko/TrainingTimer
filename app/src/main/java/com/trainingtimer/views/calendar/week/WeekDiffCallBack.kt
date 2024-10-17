package com.trainingtimer.views.calendar.week

import androidx.recyclerview.widget.DiffUtil
import com.trainingtimer.domain.CalendarDay

class WeekDiffCallBack : DiffUtil.ItemCallback<CalendarDay>() {

    override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem.dayOfMonth == newItem.dayOfMonth
    }

    override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }
}