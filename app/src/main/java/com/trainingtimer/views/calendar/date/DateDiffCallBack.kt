package com.trainingtimer.views.calendar.date

import androidx.recyclerview.widget.DiffUtil
import com.trainingtimer.domain.CalendarDay

class DateDiffCallBack: DiffUtil.ItemCallback<CalendarDay>() {

    override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem.dayOfMonth == newItem.dayOfMonth
    }

    override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }
}