package com.trainingtimer.views.calendar.week

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.PageRecyclerItemBinding
import com.trainingtimer.domain.CalendarDay
import com.trainingtimer.domain.RecyclerHeightProvider
import com.trainingtimer.utils.areDatesEqual
import com.trainingtimer.views.calendar.date.DateAdapter
import java.util.Date

class WeekAdapter(
    val context: Context,
    private val events: List<Date>,
    private val heightProvider: RecyclerHeightProvider,
    val onItemClick: (date: Date?) -> Unit
) : ListAdapter<List<CalendarDay>, WeekViewHolder>(WeekDiffCallback()) {

    private var selectedInfo: Pair<Date?, Int?> = null to null
    private val dateAdapterCache = mutableMapOf<Int, DateAdapter>()
    private var itemHeight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageRecyclerItemBinding.inflate(inflater, parent, false)
        return WeekViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val recyclerHeight = heightProvider.getRecyclerHeight()

        holder.binding.root.apply {
            doOnAttach {
                itemHeight = recyclerHeight / 6
                if (itemHeight == 0) itemHeight = screenHeight / 7
                Log.d("WeekAdapter", "recyclerHeight = $recyclerHeight")
            }
        }

        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = itemHeight
        holder.itemView.layoutParams = layoutParams

        val week = getItem(position)

        val adapter = dateAdapterCache.getOrPut(position) {
            DateAdapter(week.map { it.date }, context) { calendarDay ->
                // (events?, context, selectedInfo.first)
                onDateSelected(calendarDay.date, position)
            }
        }

        if (adapter.currentList != week) {
            adapter.submitList(week)
        }
        holder.bindHolder(adapter)
        adapter.submitList(week)
    }

    private fun onDateSelected(selectedDate: Date?, position: Int) {
        selectedInfo.second?.let { notifyItemChanged(it) }
        selectedInfo = selectedDate to position

        notifyItemChanged(position)
        onItemClick(selectedInfo.first)
    }

    fun getItemPos(selectedDate: Date): Int {
        for (index in currentList.indices) {
            val week = currentList[index]
            if (week.any { areDatesEqual(it.date, selectedDate) }) {
                return index
            }
        }
        return -1
    }
}