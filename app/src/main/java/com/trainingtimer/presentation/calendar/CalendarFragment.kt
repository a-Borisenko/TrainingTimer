package com.trainingtimer.presentation.calendar

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
import com.trainingtimer.presentation.calendar.week.CalendarAdapter
import com.trainingtimer.utils.toast
import java.util.Calendar

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()
    private val binding by lazy { FragmentCalendarBinding.inflate(layoutInflater) }
    private lateinit var adapter: CalendarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupObservers()
        setupListeners()

        binding.pageRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        PagerSnapHelper().attachToRecyclerView(binding.pageRecyclerView)
        binding.pageRecyclerView.scrollToPosition(viewModel.currentWeekNumber)
    }

    private fun setupAdapter() {
        adapter = CalendarAdapter(requireContext(), viewModel.events) { week, day ->
            requireContext().toast("week number $week, day of week $day")
        }
        binding.pageRecyclerView.adapter = adapter

        adapter.recHeight = { binding.pageRecyclerView.height }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedWeekDate.collect { selectedDate ->
                binding.monthText.text = viewModel.dateFormatter(selectedDate)
                binding.pageRecyclerView.scrollToPosition(adapter.getItemPos(selectedDate))
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loadedWeeks.collect { weeks ->
                adapter.submitList(weeks.toList())
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            pageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

            nextMonth.setOnClickListener {
//                if (viewModel.latestPos + 1 <= viewModel.loadedWeeks.value.size - 1)
                binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos + 1)
                Log.d("CalendarFragment", "next month clicked!!!")
            }

            previousMonth.setOnClickListener {
                if (viewModel.latestPos - 1 >= 0) {
                    binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos - 1)
                    Log.d("CalendarFragment", "previous month clicked!!!")
                }
            }

            monthText.setOnClickListener {
                val currentDate = Calendar.getInstance().time
                val currentWeekPos = viewModel.getWeekPositionForDate(currentDate)
                if (currentWeekPos != -1) {
                    binding.pageRecyclerView.smoothScrollToPosition(currentWeekPos)
                    viewModel.latestPos = currentWeekPos
                    Log.d("CalendarFragment", "Returning to position: $currentWeekPos")
                } else {
                    Log.d("CalendarFragment", "Current week position not found!")
                }
            }
        }
    }
}