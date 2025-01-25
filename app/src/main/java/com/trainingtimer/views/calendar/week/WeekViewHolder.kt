package com.trainingtimer.views.calendar.week

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.WeekItemBinding
import com.trainingtimer.domain.Day
import com.trainingtimer.utils.areDatesEqual
import com.trainingtimer.utils.dayOfMonth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeekViewHolder(
    val binding: WeekItemBinding,
    private val events: List<Date>
) : RecyclerView.ViewHolder(binding.root) {

//    private val daysRecycler = binding.daysRecycler
    private val weekDays = listOf(
        binding.mon, binding.tue, binding.wed, binding.thu, binding.fri, binding.sat, binding.sun
    )

    var onClick: ((week: Int, day: Int) -> Unit)? = null

    /*fun bindHolder(adapter: DayAdapter) {
        if (daysRecycler.layoutManager == null) {
            daysRecycler.layoutManager = GridLayoutManager(binding.root.context, 7)
            daysRecycler.itemAnimator = null
        }
        daysRecycler.adapter = adapter
    }*/

    /*private fun onItemClick(selectedDate: Date?, position: Int, dayPosition: Int) {

        // Обрабатываем клик на день
        (binding.root.context as? Activity)?.let {
            (it as? WeekAdapter)?.onDateSelected(selectedDate, position, dayPosition)
        }
    }*/

    fun bind(week: List<Day>, position: Int) {
        week.forEachIndexed { index, day ->
            val dateView = weekDays[index]
            dateView.text = dayOfMonth(day.date)
            dateView.setOnClickListener {
                // При клике на день передаем дату, номер недели и номер дня
//                onItemClick(day.date, position, index)
                onClick?.invoke(position, index)
            }

            // Проверяем на текущий день или день с событием
            val isToday = areDatesEqual(Calendar.getInstance().time, day.date)
            val isEventDay = events.any { areDatesEqual(it, day.date) }

            when {
                isToday -> dateView.setBackgroundResource(R.drawable.calendar_cell_today)
                isEventDay -> dateView.setBackgroundResource(R.drawable.calendar_cell_event)
                else -> dateView.setBackgroundResource(R.drawable.calendar_cell_background)
            }
        }
//        binding.root.setOnClickListener {
//            onItemClick(date)
//        }
//
//        binding.mon.text = dayOfMonth(date)
//        val isToday = areDatesEqual(Calendar.getInstance().time, date)
//        val isEventDay = events.any { areDatesEqual(it, date) }
//
//        when {
//            isToday -> binding.backgroundLinear.background =
//                ContextCompat.getDrawable(context, R.drawable.calendar_cell_today)
//
//            isEventDay -> binding.backgroundLinear.background =
//                ContextCompat.getDrawable(context, R.drawable.calendar_cell_event)
//
//            else -> binding.backgroundLinear.background =
//                ContextCompat.getDrawable(context, R.drawable.calendar_cell_background)
//        }
//
//        if (!isInTheSelectedMonth(date)) {
//            binding.mon.setTextColor(Color.parseColor("#D3D3D3"))
//        } else {
//            binding.mon.setTextColor(Color.BLACK)
//        }
    }

    private fun isInTheSelectedMonth(date: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        val currentMonth = sdf.format(Calendar.getInstance().time)
        return sdf.format(date) == currentMonth
    }
}