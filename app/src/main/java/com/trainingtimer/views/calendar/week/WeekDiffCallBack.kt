package com.trainingtimer.views.calendar.week

import androidx.recyclerview.widget.DiffUtil

class WeekDiffCallBack : DiffUtil.ItemCallback<Week>() {

    override fun areItemsTheSame(oldItem: Week, newItem: Week): Boolean {
        return oldItem.days == newItem.days
    }

    override fun areContentsTheSame(oldItem: Week, newItem: Week): Boolean {
        return oldItem == newItem
    }
}