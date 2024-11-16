package com.trainingtimer.views.calendar.week

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.PageRecyclerItemBinding
import com.trainingtimer.views.calendar.date.DateAdapter

class WeekViewHolder(val binding: PageRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

    private val daysRecycler = binding.daysRecycler

    fun bindHolder(adapter: DateAdapter) {
        if (daysRecycler.layoutManager == null) {
            daysRecycler.layoutManager = GridLayoutManager(binding.root.context, 7)
            daysRecycler.itemAnimator = null
        }
        daysRecycler.adapter = adapter
    }
}