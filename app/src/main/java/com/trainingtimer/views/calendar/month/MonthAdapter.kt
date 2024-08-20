package com.trainingtimer.views.calendar.month

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.PageRecyclerItemBinding
import com.trainingtimer.views.calendar.date.DateAdapter
import com.trainingtimer.views.calendar.date.areDatesEqual
import java.util.Date

class MonthAdapter(
    val context: Context,
    private val events: List<Date>,
    val onItemClick: (date: Date?) -> Unit
) :
    ListAdapter<Date, MonthViewHolder>(MonthDiffCallBack()) {
    var selectedDate: Date? = null
    var selectedMonthPos: Int? = null

    private val monthCalculator = MonthCalculator()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageRecyclerItemBinding.inflate(inflater, parent, false)
        return MonthViewHolder(binding)
    }

    fun getItemPos(date: Date): Int {
        var selectedDate: Date? = null
        currentList.forEach {
            if (areDatesEqual(it, date)) {
                selectedDate = it
                return@forEach
            }
        }
        return currentList.indexOf(selectedDate)
    }

    fun getItemByPos(pos: Int): Date {
        return currentList[pos]
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val date = getItem(position)
        val list = monthCalculator.getDaysInMonth(date)

        val adapter = DateAdapter(events, context, selectedDate, date) { calendarDay ->
            if (areDatesEqual(selectedDate, calendarDay.date)) {
                val lastPos = selectedMonthPos
                selectedMonthPos = null
                selectedDate = null
                notifyItemChanged(lastPos!!)
            } else {
                if (selectedMonthPos == null) {
                    selectedMonthPos = position
                    selectedDate = calendarDay.date
                } else {
                    notifyItemChanged(selectedMonthPos!!)
                    selectedMonthPos = position
                    selectedDate = calendarDay.date
                }
                notifyItemChanged(selectedMonthPos!!)
                onItemClick(selectedDate)
            }
        }
        holder.apply {
            daysRecycler.layoutManager = GridLayoutManager(context, 7)
            daysRecycler.adapter = adapter
            daysRecycler.itemAnimator = null
        }
        adapter.submitList(list)
    }
}