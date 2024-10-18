package com.trainingtimer.views.calendar.week

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.CalendarDay

class WeekAdapter : ListAdapter<CalendarDay, WeekViewHolder>(WeekDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CalendarCellBinding.inflate(inflater, parent, false)
        return WeekViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        // Рассчитываем высоту элемента
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val weekCount = 6  // Максимум 6 недель в месяце
        val itemHeight = screenHeight / weekCount  // Равномерное распределение высоты на строки

        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = itemHeight
        holder.itemView.layoutParams = layoutParams
    }
}