package com.trainingtimer.views.calendar

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentCalendarBinding
import com.trainingtimer.views.calendar.month.MonthAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: FragmentCalendarBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCalendarBinding.bind(view)

        // Start from the current month
        binding.monthText.text = viewModel.dateFormatter(viewModel.selectedMonthDate.value)

        val adapter = MonthAdapter(requireContext(), viewModel.events) {
            val sdf = SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault())
            Toast.makeText(
                requireContext(),
                "Selected date is : ${sdf.format(it)}",
                Toast.LENGTH_LONG
            ).show()
        }
        binding.pageRecyclerView.adapter = adapter
        binding.pageRecyclerView.itemAnimator = null
        adapter.submitList(viewModel.loadedDates.value)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.pageRecyclerView.layoutManager = layoutManager

        // Add PagerSnapHelper to make RecyclerView scroll like a Pager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.pageRecyclerView)

        // Start calendar from the current month
        binding.pageRecyclerView.scrollToPosition(adapter.getItemPos(viewModel.selectedMonthDate.value))

        binding.pageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Get the position of the selected month
                    val pos = layoutManager.findFirstVisibleItemPosition()

                    // If switched to a different month
                    if (viewModel.latestPos != pos) {
                        // Assign new selected date
                        viewModel.updateSelectedDate(pos)
                        binding.monthText.text = viewModel.dateFormatter(viewModel.selectedMonthDate.value)
                        // Assign new latest position
                        viewModel.latestPos = pos

                        // If at the start of the list, load previous months and scroll back to the same month
                        if (pos == 0) {
                            viewModel.loadPreviousMonths()
                            adapter.submitList(viewModel.loadedDates.value.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(viewModel.selectedMonthDate.value))
                        }
                        // If at the end of the list, load next months and scroll back to the same month
                        if (pos == viewModel.loadedDates.value.size - 1) {
                            viewModel.loadNextMonths()
                            adapter.submitList(viewModel.loadedDates.value.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(viewModel.selectedMonthDate.value))
                        }
                    }
                }
            }
        })
        binding.nextMonth.setOnClickListener {
            // Go to the next month
            if (viewModel.latestPos + 1 <= viewModel.loadedDates.value.size - 1)
                binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos + 1)
        }
        binding.previousMonth.setOnClickListener {
            // Go to the previous month
            if (viewModel.latestPos - 1 >= 0) {
                binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos - 1)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}