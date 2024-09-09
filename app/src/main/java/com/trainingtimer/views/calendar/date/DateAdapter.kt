package com.trainingtimer.views.calendar.date

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.CalendarDay
import java.util.Date

class DateAdapter(
    private val events: List<Date>,
    val context: Context,
    private val selectedDate: Date?,
    private val onItemClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, DateViewHolder>(DateDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding =
            CalendarCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding, context, events, selectedDate, onItemClick)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}