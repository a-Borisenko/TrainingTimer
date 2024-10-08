package com.trainingtimer.views.calendar.date

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateViewHolder(
    private val binding: CalendarCellBinding,
    private val context: Context,
    private val events: List<Date>,
    private val selectedDate: Date?,
    private val onItemClick: (CalendarDay) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(date: CalendarDay) {

        if (isInTheSelectedMonth(date.date)) {
            binding.root.setOnClickListener {
                onItemClick(date)
            }

            val calendar = Calendar.getInstance()
            if (areDatesEqual(calendar.time, date.date)) {
                binding.backgroundConstraint.background =
                    getDrawable(context, R.drawable.calendar_cell_today)
            } else {
                var isEventDay = false
                for (event in events) {
                    if (areDatesEqual(event, date.date)) {
                        isEventDay = true
                        break
                    }
                }
                if (isEventDay) {
                    binding.backgroundConstraint.background =
                        getDrawable(context, R.drawable.calendar_cell_event)
                } else {
                    binding.backgroundConstraint.background =
                        getDrawable(context, R.drawable.calendar_cell_background)
                }
            }
        } else {
            binding.backgroundConstraint.background =
                getDrawable(context, R.drawable.calendar_cell_gray)
            binding.textView.setTextColor(Color.parseColor("#D3D3D3"))
        }

        binding.textView.text = date.dayOfMonth
        if (selectedDate != null) {
            binding.backgroundConstraint.isSelected = areDatesEqual(selectedDate, date.date)
        }
    }

    private fun areDatesEqual(dateFirst: Date, dateSecond: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(dateFirst) == sdf.format(dateSecond)
    }

    private fun isInTheSelectedMonth(date: Date): Boolean {
        if (events.isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
            return sdf.format(date) == sdf.format(events.first())
        }
        return false
    }
}