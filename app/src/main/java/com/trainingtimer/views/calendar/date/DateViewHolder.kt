package com.trainingtimer.views.calendar.date

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.CalendarCellBinding

class DateViewHolder(val binding: CalendarCellBinding) : RecyclerView.ViewHolder(binding.root) {

    val textView = binding.textView
    val backgroundConstraint = binding.backgroundConstraint
}