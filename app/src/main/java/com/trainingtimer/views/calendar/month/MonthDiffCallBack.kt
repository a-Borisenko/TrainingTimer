package com.trainingtimer.views.calendar.month

import androidx.recyclerview.widget.DiffUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    if (dateFirst == null || dateSecond == null) {
        return false
    }
    return sdf.format(dateFirst).equals(sdf.format(dateSecond))
}

class MonthDiffCallBack : DiffUtil.ItemCallback<Date>() {

    override fun areItemsTheSame(oldItem: Date, newItem: Date): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Date, newItem: Date): Boolean {
        return oldItem == newItem
    }
}