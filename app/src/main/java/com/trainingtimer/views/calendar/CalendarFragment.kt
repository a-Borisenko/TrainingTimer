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
import com.trainingtimer.views.calendar.week.WeekAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: WeekAdapter

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
        adapter = WeekAdapter(requireContext(), viewModel.events) { selectedDate ->
            val sdf = SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault())
            Toast.makeText(
                requireContext(),
                "Selected date is: ${sdf.format(selectedDate)}",
                Toast.LENGTH_LONG
            ).show()
        }
        binding.pageRecyclerView.adapter = adapter
        binding.pageRecyclerView.itemAnimator = null
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedWeekDate.collect { selectedDate ->
                binding.monthText.text = viewModel.dateFormatter(selectedDate)
                val weekPosition = adapter.getItemPos(selectedDate)
                binding.pageRecyclerView.scrollToPosition(weekPosition)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loadedWeeks.collect { weeks ->
                adapter.submitList(weeks)
            }
        }
    }

    private fun setupListeners() {
        binding.pageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = binding.pageRecyclerView.layoutManager as LinearLayoutManager
                    val pos = layoutManager.findFirstVisibleItemPosition()

                    if (viewModel.latestPos != pos) {
                        viewModel.updateSelectedWeek(pos)
                        viewModel.latestPos = pos
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
            binding.pageRecyclerView.smoothScrollToPosition(viewModel.currentWeekNumber)
            Log.d("CalendarFragment", "position is ${viewModel.currentWeekNumber}")
        }
    }
}