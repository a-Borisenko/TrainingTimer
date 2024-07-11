package com.trainingtimer.views.calendar

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.CalendarCellBinding

class CalendarViewHolder(val binding: CalendarCellBinding) : RecyclerView.ViewHolder(binding.root)/*, View.OnClickListener*/ {

    val dayOfMonth = binding.cellDayText

//    val onItemListener: CalendarAdapter.OnItemListener? = null
    /*fun CalendarViewHolder(itemView: View, onItemListener: CalendarAdapter.OnItemListener?) {
        super(itemView)
        dayOfMonth = itemView.findViewById<TextView>(R.id.cellDayText)
        this.onItemListener = onItemListener
        itemView.setOnClickListener(this)
    }*/

    /*override fun onClick(view: View?) {
        onItemListener.onItemClick(getAdapterPosition(), dayOfMonth.text as String)
    }*/
}