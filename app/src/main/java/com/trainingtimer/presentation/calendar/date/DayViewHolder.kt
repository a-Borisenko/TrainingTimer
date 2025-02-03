package com.trainingtimer.presentation.calendar.date

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.entity.Day
import com.trainingtimer.utils.areDatesEqual
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DayViewHolder(
    private val binding: CalendarCellBinding,
    private val context: Context,
    private val events: List<Date>,
    private val onItemClick: (Day) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(date: Day) {
        binding.root.setOnClickListener {
            onItemClick(date)
        }

        binding.textView.text = date.dayOfMonth
        val isToday = areDatesEqual(Calendar.getInstance().time, date.date)
        val isEventDay = events.any { areDatesEqual(it, date.date) }

        when {
            isToday -> binding.backgroundConstraint.background =
                getDrawable(context, R.drawable.calendar_cell_today)

            isEventDay -> binding.backgroundConstraint.background =
                getDrawable(context, R.drawable.calendar_cell_event)

            else -> binding.backgroundConstraint.background =
                getDrawable(context, R.drawable.calendar_cell_background)
        }

        if (!isInTheSelectedMonth(date.date)) {
            binding.textView.setTextColor(Color.parseColor("#D3D3D3"))
        } else {
            binding.textView.setTextColor(Color.BLACK)
        }
    }

    private fun isInTheSelectedMonth(date: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        val currentMonth = sdf.format(Calendar.getInstance().time)
        return sdf.format(date) == currentMonth
    }
}