package com.trainingtimer.views.calendar

import androidx.recyclerview.widget.DiffUtil
import com.trainingtimer.domain.Day

class CalendarDiffCallback: DiffUtil.ItemCallback<Day>() {

    override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean {
        return oldItem.dayOfMonth == newItem.dayOfMonth
    }

    override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean {
        return oldItem == newItem
    }
}