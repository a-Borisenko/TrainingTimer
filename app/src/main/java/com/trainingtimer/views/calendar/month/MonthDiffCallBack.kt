package com.trainingtimer.views.calendar.month

import androidx.recyclerview.widget.DiffUtil
import java.util.Date

class MonthDiffCallBack : DiffUtil.ItemCallback<Date>() {

    override fun areItemsTheSame(oldItem: Date, newItem: Date): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Date, newItem: Date): Boolean {
        return oldItem == newItem
    }

}