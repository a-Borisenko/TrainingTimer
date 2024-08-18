package com.trainingtimer.views.calendar.date

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.R
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateAdapter(
    val events: List<Date>,
    val context: Context,
    val selectedDate: Date?,
    val currentMonth: Date,
    val onItemClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, DateViewHolder>(DateDiffCallBack()) {

    fun areDatesEqual(dateFirst: Date, dateSecond: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(dateFirst).equals(sdf.format(dateSecond))
    }

    fun isInTheSelectedMonth(dateFirst: Date, dateSecond: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        return sdf.format(dateFirst).equals(sdf.format(dateSecond))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding =
            CalendarCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {

        val date = getItem(position)

        if (isInTheSelectedMonth(date.date, currentMonth)) {

            holder.binding.root.setOnClickListener {
                // only click current month days
                onItemClick(date)
            }

            val calendar = Calendar.getInstance()
            // if date is inside the selected month, set background resource
            if (areDatesEqual(calendar.time, date.date)) {
                // Today
                holder.backgroundConstraint.background =
                    ContextCompat.getDrawable(context, R.drawable.calendar_cell_today)
            } else {
                var isEventDay = false
                events.forEach {
                    if (areDatesEqual(it, date.date)) {
                        isEventDay = true
                        return@forEach
                    }
                }
                if (isEventDay) {
                    // event day
                    holder.backgroundConstraint.background =
                        ContextCompat.getDrawable(context, R.drawable.calendar_cell_event)
                } else {
                    // normal day
                    holder.backgroundConstraint.background =
                        ContextCompat.getDrawable(context, R.drawable.calendar_cell_background)
                }
            }
        } else {
            // if not in the selected month, display gray
            holder.backgroundConstraint.background =
                ContextCompat.getDrawable(context, R.drawable.calendar_cell_gray)
            holder.textView.setTextColor(Color.parseColor("#D3D3D3"))
        }
        holder.textView.text = date.dayOfMonth
        if (selectedDate != null) {
            holder.backgroundConstraint.isSelected = areDatesEqual(selectedDate, date.date)
        }
    }
}