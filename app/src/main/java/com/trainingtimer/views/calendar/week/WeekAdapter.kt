package com.trainingtimer.views.calendar.week

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.WeekItemBinding
import com.trainingtimer.domain.Day
import com.trainingtimer.utils.areDatesEqual
import java.util.Date

class WeekAdapter(
    val context: Context,
    private val events: List<Date>,
    val onItemClick: (date: Date?) -> Unit
) : ListAdapter<List<Day>, WeekViewHolder>(WeekDiffCallback()) {

    private var selectedInfo: Pair<Date?, Int?> = null to null
//    private val dayAdapterCache = mutableMapOf<Int, DayAdapter>()

    var recHeight: () -> Int = { 0 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WeekItemBinding.inflate(inflater, parent, false)
        return WeekViewHolder(binding, events)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        holder.itemView.layoutParams.height = recHeight() / 6
        val week = getItem(position)

        /*val adapter = dayAdapterCache.getOrPut(position) {
            DayAdapter(events, context) { calendarDay ->
                onDateSelected(calendarDay.date, position)
            }
        }

        if (adapter.currentList != week) {
            adapter.submitList(week)
        }*/
        holder.bind(week, position)
    }

    fun onDateSelected(selectedDate: Date?, position: Int) {
        selectedInfo.second?.let { notifyItemChanged(it) }
        selectedInfo = selectedDate to position

        notifyItemChanged(position)
        onItemClick(selectedInfo.first)
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