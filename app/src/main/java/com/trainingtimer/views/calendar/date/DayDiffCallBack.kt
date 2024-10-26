package com.trainingtimer.views.calendar.date

import androidx.recyclerview.widget.DiffUtil
import com.trainingtimer.domain.CalendarDay

class DayDiffCallBack : DiffUtil.ItemCallback<CalendarDay>() {
    override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }
}