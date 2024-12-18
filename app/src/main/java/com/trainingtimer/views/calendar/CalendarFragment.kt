package com.trainingtimer.views.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentCalendarBinding
import com.trainingtimer.domain.RecyclerHeightProvider
import java.text.DateFormat
import java.util.Calendar

class CalendarFragment : Fragment(R.layout.fragment_calendar), RecyclerHeightProvider {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: FragmentCalendarBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)

        setupAdapter()
        setupObservers()
        setupListeners()

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.pageRecyclerView.layoutManager = layoutManager

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.pageRecyclerView)
        binding.pageRecyclerView.scrollToPosition(viewModel.currentWeekNumber)
    }

    private fun setupAdapter() {
        val adapter =
            viewModel.setupAdapter(requireContext(), this) { selectedDate ->
                Toast.makeText(
                    requireContext(),
                    "Selected date is: ${
                        selectedDate?.let {
                            DateFormat.getDateInstance().format(it)
                        } ?: "no data"
                    }",
                    Toast.LENGTH_LONG).show()
            }
        binding.pageRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedWeekDate.collect { selectedDate ->
                binding.monthText.text = viewModel.dateFormatter(selectedDate)
                val weekPosition = viewModel.adapter.getItemPos(selectedDate)
                binding.pageRecyclerView.scrollToPosition(weekPosition)
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

                    Log.d(
                        "onScrollStateChanged",
                        "Current position: $pos,\nLoaded weeks size: ${viewModel.loadedWeeks.value.size}"
                    )

                    if (viewModel.latestPos != pos) {
                        viewModel.updateSelectedWeek(pos)
                        viewModel.latestPos = pos

                        if (pos == 0) {
                            Log.d("onScrollStateChanged", "Loading previous weeks")
                            viewModel.loadPreviousWeeks()
                        } else if (pos == viewModel.loadedWeeks.value.size - 1) {
                            Log.d("onScrollStateChanged", "Loading next weeks")
                            viewModel.loadNextWeeks()
                        }
                    }
                }
            }
        })

        binding.nextMonth.setOnClickListener {
            if (viewModel.latestPos + 1 <= viewModel.loadedWeeks.value.size - 1)
                binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos + 1)
        }

        binding.previousMonth.setOnClickListener {
            if (viewModel.latestPos - 1 >= 0) {
                binding.pageRecyclerView.smoothScrollToPosition(viewModel.latestPos - 1)
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

    override fun getRecyclerHeight(): Int {
        return binding.pageRecyclerView.height
    }
}