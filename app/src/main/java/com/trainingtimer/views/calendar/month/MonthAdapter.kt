package com.trainingtimer.views.calendar.month

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.PageRecyclerItemBinding
import com.trainingtimer.domain.CalendarDay
import com.trainingtimer.views.calendar.CalendarAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MonthAdapter(
    val context: Context,
    private val events: List<Date>,
    val onItemClick: (date: Date?) -> Unit
) :
    ListAdapter<Date, MonthViewHolder>(MonthDiffCallBack()) {
    var selectedDate: Date? = null
    var selectedMonthPos: Int? = null


    private fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
        // Function to compare two dates are the same day
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        if (dateFirst == null || dateSecond == null) {
            return false
        }
        return sdf.format(dateFirst).equals(sdf.format(dateSecond))
    }

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

    private fun daysInMonthList(date: Date): List<CalendarDay> {
        val daysInMonthList: MutableList<CalendarDay> = mutableListOf()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2
        when(dayOfWeek){
            -1 -> {
                // SUNDAY
                calendar.add(Calendar.DAY_OF_MONTH,-6)
                while(daysInMonthList.size < 42){
                    daysInMonthList.add(CalendarDay(
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        calendar.time
                    ))
                    calendar.add(Calendar.DAY_OF_MONTH,1)
                }
            }
            else -> {
                // OTHER DAYS
                calendar.add(Calendar.DAY_OF_MONTH,-dayOfWeek)
                while(daysInMonthList.size < 42){
                    daysInMonthList.add(CalendarDay(
                        calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        calendar.time
                    ))
                    calendar.add(Calendar.DAY_OF_MONTH,1)
                }
            }
        }
        return daysInMonthList
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val date = getItem(position)
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
                    selectedMonthPos = position
                    selectedDate = calendarDay.date
                } else {

                    // If already selected a day before, switch the selected date
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