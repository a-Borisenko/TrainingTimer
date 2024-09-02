package com.trainingtimer.views.calendar.date

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.CalendarDay
import java.util.Date

class DateAdapter(
    events: List<Date>,
    val context: Context,
    private val selectedDate: Date?,
    private val currentMonth: Date,
    val onItemClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, DateViewHolder>(DateDiffCallBack()) {

    private val cellStyleProvider = CalendarCellStyleProvider(events)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding =
            CalendarCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val calendarDay = getItem(position)
        configureViewHolder(holder, calendarDay)
    }

    private fun configureViewHolder(holder: DateViewHolder, calendarDay: CalendarDay) {
        holder.binding.root.setOnClickListener {
            // only click current month days
            onItemClick(calendarDay)
        }

        holder.backgroundConstraint.background = getBackgroundResource(calendarDay.date)
        holder.textView.text = calendarDay.dayOfMonth

        holder.backgroundConstraint.isSelected = selectedDate?.let {
            areDatesEqual(it, calendarDay.date)
        } ?: false
    }

    private fun getBackgroundResource(date: Date): Drawable? {
        val backgroundResource = cellStyleProvider.getBackgroundResourceForCell(date, currentMonth)
        return ContextCompat.getDrawable(context, backgroundResource)
    }
}