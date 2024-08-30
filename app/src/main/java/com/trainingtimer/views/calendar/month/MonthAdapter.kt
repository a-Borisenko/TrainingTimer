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
) : ListAdapter<Date, MonthViewHolder>(MonthDiffCallBack()) {

    var selectedDate: Date? = null
    var selectedMonthPos: Int? = null

    private val monthCalculator = MonthCalculator()
    private val dateAdapterCache = mutableMapOf<Int, DateAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageRecyclerItemBinding.inflate(inflater, parent, false)
        return MonthViewHolder(binding)
    }

    fun getItemPos(date: Date): Int {
        return currentList.indexOf(currentList.find { areDatesEqual(it, date) })
    }

    fun getItemByPos(pos: Int): Date {
        return currentList[pos]
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val date = getItem(position)
        val list = monthCalculator.getDaysInMonth(date)

        val adapter = dateAdapterCache.getOrPut(position) {
            DateAdapter(events, context, selectedDate, date) { calendarDay ->
                onDateSelected(calendarDay.date, position)
            }
        }

        holder.apply {
            daysRecycler.layoutManager = GridLayoutManager(context, 7)
            daysRecycler.adapter = adapter
            daysRecycler.itemAnimator = null
        }
        adapter.submitList(list)
    }

    private fun onDateSelected(selectedDate: Date?, position: Int) {
        if (areDatesEqual(this.selectedDate, selectedDate)) {
            val lastPos = selectedMonthPos
            selectedMonthPos = null
            this.selectedDate = null
            notifyItemChanged(lastPos!!)
        } else {
            if (selectedMonthPos == null) {
                selectedMonthPos = position
                this.selectedDate = selectedDate
            } else {
                notifyItemChanged(selectedMonthPos!!)
                selectedMonthPos = position
                this.selectedDate = selectedDate
            }
            notifyItemChanged(selectedMonthPos!!)
            onItemClick(this.selectedDate)
        }
    }
}