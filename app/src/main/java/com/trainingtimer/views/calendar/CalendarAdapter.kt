package com.trainingtimer.views.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.CalendarCellBinding
import com.trainingtimer.domain.Day

class CalendarAdapter : ListAdapter<Day, CalendarViewHolder>(CalendarDiffCallback()) {

    var onDayLongClickListener: ((Day) -> Unit)? = null
    var onDayClickListener: ((Day) -> Unit)? = null

//    private val daysOfMonth: ArrayList<String>? = null
//    private val onItemListener: OnItemListener? = null

    /*fun CalendarAdapter(daysOfMonth: ArrayList<String>?, onItemListener: OnItemListener?) {
        this.daysOfMonth = daysOfMonth
        this.onItemListener = onItemListener
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CalendarCellBinding.inflate(inflater, parent, false)

//        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
//        val layoutParams = view.layoutParams
//        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = getItem(position)
        holder.dayOfMonth.text = day.dayOfMonth
//            daysOfMonth?.get(position) ?: "day not found"

        /*with(holder) {
            listSets.text = day.sets.toString()
            listTitle.text = training.title
            listTimes.text = training.times
            listRest.text = training.rest
        }*/
        holder.binding.root.setOnClickListener {
            onDayClickListener?.invoke(day)
        }
        holder.binding.root.setOnLongClickListener {
            onDayLongClickListener?.invoke(day)
            true
        }
    }

    /*override fun getItemCount(): Int {
        return daysOfMonth!!.size
    }*/

    /*interface OnItemListener {
        fun onItemClick(position: Int, dayText: String?)
    }*/
}