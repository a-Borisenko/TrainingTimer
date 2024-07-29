package com.trainingtimer.views.calendar

import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var listAdapter: CalendarAdapter
    private lateinit var binding: FragmentCalendarBinding

    private lateinit var monthYearText: TextView
//    private val calendarRecyclerView: RecyclerView? = null
    private lateinit var selectedDate: LocalDate
    private val loadedDates : MutableList<Date> = mutableListOf()
    private lateinit var selectedMonthDate: Date
    var latestPos = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCalendarBinding.bind(view)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val rvDayList = binding.calendarRecyclerView
        listAdapter = CalendarAdapter()
        rvDayList.adapter = listAdapter
        setupClickListener()
//        setupSwipeListener(rvDayList)


//        val today = Calendar.getInstance()

//        binding.calendarRecyclerView.date = today.timeInMillis

        /*binding.calendarRecyclerView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Обработка выбранной даты
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            Toast.makeText(context, "date $dayOfMonth.$month.$year", Toast.LENGTH_SHORT).show()
            // Выполните необходимые действия с выбранной датой
        }*/

        val calendar = Calendar.getInstance()

        // Start from the current month
        selectedMonthDate = calendar.time
        binding.monthYearTv.text = dateFormatter(selectedMonthDate!!)

        // Initiate the months list
//        startList()

        // Start from the center of the list
        latestPos = loadedDates.size/2

        // Create dummy events list
        val events = mutableListOf<Date>()
        for(i in 1..10){
            calendar.add(java.util.Calendar.DATE,i)
            events.add(calendar.time)
        }

        /*val adapter = PageAdapter(requireContext(),events){
            val sdf = SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault())
            Toast.makeText(requireContext(),"Selected date is : ${sdf.format(it)}",Toast.LENGTH_LONG).show()
        }*/
//        binding.pageRecyclerView.adapter = adapter
        binding.calendarRecyclerView.itemAnimator = null
        listAdapter.submitList(loadedDates)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
        binding.calendarRecyclerView.layoutManager = layoutManager

        // Add PagerSnapHelper to make RecyclerView scroll like a Pager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarRecyclerView)

        // Start calendar from the current month
        binding.calendarRecyclerView.scrollToPosition(listAdapter.getItemPos(selectedMonthDate!!))

        binding.calendarRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    // Get the position of the selected month
                    val pos = layoutManager.findFirstVisibleItemPosition()

                    // If switched to a different month
                    if(latestPos != pos){
                        // Assign new selected date
                        selectedMonthDate = adapter.getItemByPos(pos)
                        binding.monthText.text = dateFormatter(selectedMonthDate!!)
                        // Assign new latest position
                        latestPos = pos

                        // If at the start of the list, load previous months and scroll back to the same month
                        if(pos == 0){
                            loadPreviousMonths()
                            adapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate!!))
                        }
                        // If at the end of the list, load next months and scroll back to the same month
                        if(pos == loadedDates.size-1){
                            loadNextMonths()
                            adapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate!!))
                        }
                    }
                }
            }
        })
        binding.nextMonth.setOnClickListener{
            // Go to the next month
            if(latestPos +1 <= loadedDates.size-1)
                binding.calendarRecyclerView.smoothScrollToPosition(latestPos+1)
        }
        binding.previousMonth.setOnClickListener {
            // Go to the previous month
            if(latestPos - 1 >= 0){
                binding.calendarRecyclerView.smoothScrollToPosition(latestPos-1)
            }
        }
    }

    private fun setupClickListener() {
        binding.previousMonth.setOnClickListener {
            previousMonthAction()
        }
        binding.nextMonth.setOnClickListener {
            nextMonthAction()
        }

        listAdapter.onDayClickListener = {
            Toast.makeText(context, "${it.dayOfMonth} clicked!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMonthView() {
        val calendar = Calendar.getInstance()
        calendar.set(
            2020,
            6,
            27
        )
//        calendarView.date = calendar.timeInMillis

        /*binding.monthYearTV.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(getApplicationContext(), 7)
        calendarRecyclerView.setLayoutManager(layoutManager)
        calendarRecyclerView.setAdapter(calendarAdapter)*/
    }

    // TODO: move to ViewModel
    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        /*val selectedDate = calendarView.date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
        textView.text = "Selected date: ${dateFormatter.format(calendar.time)}"*/

        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    // TODO: move to ViewModel
    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private fun previousMonthAction() {
        Toast.makeText(context, "previous month clicked!!!", Toast.LENGTH_SHORT).show()
    }

    private fun nextMonthAction() {
        Toast.makeText(context, "next month clicked!!!", Toast.LENGTH_SHORT).show()
    }

    /*fun onItemClick(position: Int, dayText: String) {
        if (dayText != "") {
            val message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }*/
}