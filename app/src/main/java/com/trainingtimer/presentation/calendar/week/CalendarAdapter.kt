package com.trainingtimer.presentation.calendar.week

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.WeekItemBinding
import com.trainingtimer.domain.entity.Day
import com.trainingtimer.utils.areDatesEqual
import java.util.Date

class CalendarAdapter(
    val context: Context,
    private val events: List<Date>,
    private val onItemClick: (week: String, day: String) -> Unit
) : ListAdapter<List<Day>, CalendarViewHolder>(CalendarDiffCallback()) {

    var recHeight: () -> Int = { 0 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WeekItemBinding.inflate(inflater, parent, false)
        return CalendarViewHolder(binding, events, onItemClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.itemView.layoutParams.height = recHeight() / 6
        val week = getItem(position)
        holder.bind(week)
    }

    fun getItemPos(selectedDate: Date): Int {
        for (index in currentList.indices) {
            val week = currentList[index]
            if (week.any { areDatesEqual(it.date, selectedDate) }) {
                return index
            }
        }
        return -1
    }
}