package com.trainingtimer.views.calendar.week

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.views.calendar.date.DayAdapter

class WeekViewHolder(val binding: CalendarCellBinding) : RecyclerView.ViewHolder(binding.root) {

    val date = binding.textView

    private val dayAdapter = DayAdapter()

    init {
//        binding.daysRecyclerView.layoutManager = GridLayoutManager(binding.root.context, 7)
//        binding.daysRecyclerView.adapter = dayAdapter
    }

    fun bind(week: Week) {
        dayAdapter.submitList(week.days)
    }
}