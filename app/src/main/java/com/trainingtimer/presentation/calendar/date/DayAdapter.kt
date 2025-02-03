package com.trainingtimer.presentation.calendar.date

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.entity.Day
import java.util.Date

class DayAdapter(
    private val events: List<Date>,
    val context: Context,
    private val onItemClick: (Day) -> Unit
) : ListAdapter<Day, DayViewHolder>(DayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding =
            CalendarCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding, context, events, onItemClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}