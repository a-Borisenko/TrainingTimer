package com.trainingtimer.views.calendar.week

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.doOnAttach
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.PageRecyclerItemBinding
import com.trainingtimer.domain.CalendarDay
import com.trainingtimer.views.calendar.date.DateAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    if (dateFirst == null || dateSecond == null) return false
    return sdf.format(dateFirst) == sdf.format(dateSecond)
}

class WeekAdapter(
    val context: Context,
    private val events: List<Date>,
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

        holder.binding.root.apply {
            doOnAttach {
                itemHeight = if (isGesture(it)) {
                    screenHeight / 7
                } else {
                    screenHeight / 8
                }
            }
        }

        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = itemHeight
        holder.itemView.layoutParams = layoutParams

        val week = getItem(position)

        val adapter = dateAdapterCache.getOrPut(position) {
            DateAdapter(week.map { it.date }, context) { calendarDay ->
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

    private fun isGesture(view: View): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                (view.rootWindowInsets?.getInsets(WindowInsets.Type.systemGestures())?.left
                    ?: 0) > 0
            } else {
                TODO("VERSION.SDK_INT = Q")
            }
        } else false
    }
}