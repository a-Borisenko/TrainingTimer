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
    private val onItemClick: (CalendarDay) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(date: CalendarDay) {
        // Всегда устанавливаем клик
        binding.root.setOnClickListener {
            onItemClick(date)
        }

        // Установка текста дня
        binding.textView.text = date.dayOfMonth

        // Проверка: дата совпадает с текущей?
        val isToday = areDatesEqual(Calendar.getInstance().time, date.date)

        // Проверка: день содержит событие?
        val isEventDay = events.any { areDatesEqual(it, date.date) }

        // Настройка стиля в зависимости от условий
        when {
            isToday -> binding.backgroundConstraint.background =
                getDrawable(context, R.drawable.calendar_cell_today)

            isEventDay -> binding.backgroundConstraint.background =
                getDrawable(context, R.drawable.calendar_cell_event)

            else -> binding.backgroundConstraint.background =
                getDrawable(context, R.drawable.calendar_cell_background)
        }

        // Оформление для дат вне текущего месяца
        if (!isInTheSelectedMonth(date.date)) {
            binding.textView.setTextColor(Color.parseColor("#D3D3D3"))
        } else {
            binding.textView.setTextColor(Color.BLACK)
        }
    }

    private fun areDatesEqual(dateFirst: Date, dateSecond: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(dateFirst) == sdf.format(dateSecond)
    }

    private fun isInTheSelectedMonth(date: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        val currentMonth = sdf.format(Calendar.getInstance().time)
        return sdf.format(date) == currentMonth
    }
}