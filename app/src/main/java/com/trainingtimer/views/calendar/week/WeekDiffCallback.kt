package com.trainingtimer.views.calendar.week

import androidx.recyclerview.widget.DiffUtil
import com.trainingtimer.domain.Day

class WeekDiffCallback : DiffUtil.ItemCallback<List<Day>>() {

    override fun areItemsTheSame(oldItem: List<Day>, newItem: List<Day>): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: List<Day>, newItem: List<Day>): Boolean {
        return oldItem == newItem
    }
}