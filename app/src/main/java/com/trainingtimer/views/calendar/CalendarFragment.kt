package com.trainingtimer.views.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentCalendarBinding
import com.trainingtimer.utils.toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: FragmentCalendarBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)

        setupAdapter()
        setupObservers()
        setupListeners()

        binding.pageRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        PagerSnapHelper().attachToRecyclerView(binding.pageRecyclerView)
        binding.pageRecyclerView.scrollToPosition(viewModel.currentWeekNumber)
    }

    private fun setupAdapter() {
        val sdf = SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault())
        val adapter =
            viewModel.setupAdapter(requireContext()) { selectedDate ->
                requireContext().toast(
                    "Selected date is: ${selectedDate?.let { sdf.format(it) } ?: "no data"}"
                )
            }
        binding.pageRecyclerView.adapter = adapter

        adapter.recHeight = {
            binding.pageRecyclerView.height
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedWeekDate.collect { selectedDate ->
                binding.monthText.text = viewModel.dateFormatter(selectedDate)
                binding.pageRecyclerView.scrollToPosition(viewModel.adapter.getItemPos(selectedDate))
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loadedWeeks.collect { weeks ->
                viewModel.adapter.submitList(weeks.toList())
            }
        }
    }

    private fun setupListeners() {
        binding.pageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager =
                        binding.pageRecyclerView.layoutManager as LinearLayoutManager
                    val pos = layoutManager.findFirstVisibleItemPosition()

                    if (viewModel.latestPos != pos) {
                        viewModel.updateSelectedWeek(pos)

                        if (pos == 0) {
                            viewModel.loadPreviousWeeks()
                        } else if (pos == viewModel.loadedWeeks.value.size - 1) {
                            viewModel.loadNextWeeks()
                        }
                    }
                }
            }
        })

        binding.nextMonth.setOnClickListener {
            if (viewModel.latestPos + 1 <= viewModel.loadedWeeks.value.size - 1)
                binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos + 1)
            Log.d("CalendarFragment", "next month clicked!!!")
        }

        binding.previousMonth.setOnClickListener {
            if (viewModel.latestPos - 1 >= 0) {
                binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos - 1)
                Log.d("CalendarFragment", "previous month clicked!!!")
            }
        }

        binding.monthText.setOnClickListener {
            val currentDate = Calendar.getInstance().time
            val currentWeekPos = viewModel.getWeekPositionForDate(currentDate)
            if (currentWeekPos != -1) {
                binding.pageRecyclerView.smoothScrollToPosition(currentWeekPos)
                viewModel.latestPos = currentWeekPos
                Log.d("CalendarFragment", "Returning to position: $currentWeekPos")
            } else {
                Log.e("CalendarFragment", "Current week position not found!")
            }
        }
    }
}