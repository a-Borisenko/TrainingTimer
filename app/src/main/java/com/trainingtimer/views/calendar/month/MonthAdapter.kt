package com.trainingtimer.views.calendar.month

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.doOnAttach
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.PageRecyclerItemBinding
import com.trainingtimer.views.calendar.date.DateAdapter
import java.util.Date

class MonthAdapter(
    val context: Context,
    private val events: List<Date>,
    val onItemClick: (date: Date?) -> Unit
) : ListAdapter<Date, MonthViewHolder>(MonthDiffCallBack()) {

    private var selectedInfo: Pair<Date?, Int?> = null to null
    private val monthCalculator = MonthCalculator()
    private val dateAdapterCache = mutableMapOf<Int, DateAdapter>()
    private var itemHeight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageRecyclerItemBinding.inflate(inflater, parent, false)
        return MonthViewHolder(binding)
    }

    fun getItemPos(date: Date): Int {
        return currentList.indexOfFirst { areDatesEqual(it, date) }
    }

    fun getItemByPos(pos: Int): Date {
        return currentList[pos]
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        // Рассчитываем высоту элемента
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val weekCount = 6  // Максимум 6 недель в месяце

        holder.binding.root.apply {
            doOnAttach {
                if (isGesture(it)) {
                    itemHeight = screenHeight / weekCount  // Равномерное распределение высоты на строки
                } else {
                    TODO("screen set for 3 buttons")
                }
            }
        }

        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = itemHeight
        holder.itemView.layoutParams = layoutParams

        val date = getItem(position)
        val list = monthCalculator.getDaysInMonth(date)

        val adapter = dateAdapterCache.getOrPut(position) {
            DateAdapter(events, context, selectedInfo.first) { calendarDay ->
                onDateSelected(calendarDay.date, position)
            }
        }
        holder.bindHolder(adapter)
        adapter.submitList(list)
    }

    private fun isGesture(view: View): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                (view.rootWindowInsets?.getInsets(WindowInsets.Type.systemGestures())?.left
                    ?: 0) > 0
            } else {
                TODO("VERSION.SDK_INT < R")
            }
        } else false
    }

    private fun MonthViewHolder.bindHolder(adapter: DateAdapter) {
        if (daysRecycler.layoutManager == null) {
            daysRecycler.layoutManager = GridLayoutManager(context, 7)
            daysRecycler.itemAnimator = null
        }
        daysRecycler.adapter = adapter
    }

    private fun onDateSelected(selectedDate: Date?, position: Int) {
        if (areDatesEqual(selectedInfo.first, selectedDate)) {
            notifyItemChanged(selectedInfo.second!!)
            selectedInfo = null to null
        } else {
            selectedInfo.second?.let { notifyItemChanged(it) }
            selectedInfo = selectedDate to position
            notifyItemChanged(position)
            onItemClick(selectedInfo.first)
        }
    }
}