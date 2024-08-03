package com.trainingtimer.views.calendar.month

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.PageRecyclerItemBinding
import com.trainingtimer.views.calendar.CalendarAdapter
import java.util.Date

class MonthViewHolder(private val binding: PageRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(date: Date) {

        // Get the month for the given date
        val list = daysInMonthList(date)

        val adapter = CalendarAdapter(events, context, selectedDate, date) { calendarDay ->

            // Single selection
            // If clicked on the selected day, unselect
            if (areDatesEqual(selectedDate, calendarDay.date)) {
                val lastPos = selectedMonthPos
                selectedMonthPos = null
                selectedDate = null
                notifyItemChanged(lastPos!!)
            } else {

                // If clicked on an unselected day, select
                // If not selected a day before, select
                if (selectedMonthPos == null) {
                    selectedMonthPos = adapterPosition
                    selectedDate = calendarDay.date
                } else {

                    // If already selected a day before, switch the selected date
                    notifyItemChanged(selectedMonthPos!!)
                    selectedMonthPos = adapterPosition
                    selectedDate = calendarDay.date
                }
                notifyItemChanged(selectedMonthPos!!)
                onItemClick(selectedDate)
            }
        }
        binding.apply {
            daysRecycler.layoutManager = GridLayoutManager(context, 7)
            daysRecycler.adapter = adapter
            daysRecycler.itemAnimator = null
        }
        adapter.submitList(list)
    }
}