package com.trainingtimer.views.calendar.date

import android.content.Context
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
        val date = getItem(position)

        holder.binding.root.setOnClickListener {
            // only click current month days
            onItemClick(date)
        }

        val backgroundResource = cellStyleProvider.getBackgroundResourceForCell(
            date.date, currentMonth
        )
        holder.backgroundConstraint.background =
            ContextCompat.getDrawable(context, backgroundResource)

        holder.textView.text = date.dayOfMonth
        if (selectedDate != null) {
            holder.backgroundConstraint.isSelected = areDatesEqual(selectedDate, date.date)
        }
    }
}