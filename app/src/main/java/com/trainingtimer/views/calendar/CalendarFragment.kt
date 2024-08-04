package com.trainingtimer.views.calendar

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentCalendarBinding
import com.trainingtimer.views.calendar.month.MonthAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val loadedDates : MutableList<Date> = mutableListOf()
    private lateinit var selectedMonthDate : Date
    private lateinit var binding: FragmentCalendarBinding

    var latestPos = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val calendar = java.util.Calendar.getInstance()
        binding = FragmentCalendarBinding.bind(view)

        // Start from the current month
        selectedMonthDate = calendar.time
        binding.monthText.text = dateFormatter(selectedMonthDate)

        // Initiate the months list
        startList()

        // Start from the center of the list
        latestPos = loadedDates.size/2

        // Create dummy events list
        val events = mutableListOf<Date>()
        for(i in 1..10){
            calendar.add(java.util.Calendar.DATE,i)
            events.add(calendar.time)
        }

        val adapter = MonthAdapter(requireContext(),events){
            val sdf = SimpleDateFormat("EE dd/MM/yyyy",Locale.getDefault())
            Toast.makeText(requireContext(),"Selected date is : ${sdf.format(it)}",Toast.LENGTH_LONG).show()
        }
        binding.pageRecyclerView.adapter = adapter
        binding.pageRecyclerView.itemAnimator = null
        adapter.submitList(loadedDates)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
        binding.pageRecyclerView.layoutManager = layoutManager

        // Add PagerSnapHelper to make RecyclerView scroll like a Pager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.pageRecyclerView)

        // Start calendar from the current month
        binding.pageRecyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate))

        binding.pageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    // Get the position of the selected month
                    val pos = layoutManager.findFirstVisibleItemPosition()

                    // If switched to a different month
                    if(latestPos != pos){
                        // Assign new selected date
                        selectedMonthDate = adapter.getItemByPos(pos)
                        binding.monthText.text = dateFormatter(selectedMonthDate)
                        // Assign new latest position
                        latestPos = pos

                        // If at the start of the list, load previous months and scroll back to the same month
                        if(pos == 0){
                            loadPreviousMonths()
                            adapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate))
                        }
                        // If at the end of the list, load next months and scroll back to the same month
                        if(pos == loadedDates.size-1){
                            loadNextMonths()
                            adapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate))
                        }
                    }
                }
            }
        })
        binding.nextMonth.setOnClickListener{
            // Go to the next month
            if(latestPos +1 <= loadedDates.size-1)
                binding.pageRecyclerView.smoothScrollToPosition(latestPos+1)
        }
        binding.previousMonth.setOnClickListener {
            // Go to the previous month
            if(latestPos - 1 >= 0){
                binding.pageRecyclerView.smoothScrollToPosition(latestPos-1)
            }
        }
        super.onViewCreated(view, savedInstanceState)


    }

    private fun dateFormatter(date : Date) : String {
        val sdf = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun startList(){
        // Initiate the calendar months list
        val calendar = java.util.Calendar.getInstance()
        calendar.time = selectedMonthDate
        val currentDate = calendar.time

        // Add 5 months before current month
        for(i in -5..-1){
            calendar.add(java.util.Calendar.MONTH,i)
            loadedDates.add(calendar.time)
            calendar.time = currentDate
        }
        // Add current month
        loadedDates.add(currentDate)
        calendar.time = currentDate
        // Add 5 months after current month
        for(i in 1..5){
            calendar.add(java.util.Calendar.MONTH,i)
            loadedDates.add(calendar.time)
            calendar.time = currentDate
        }
    }

    private fun loadPreviousMonths(){
        val calendar = java.util.Calendar.getInstance()
        calendar.time = loadedDates[0]

        // Load the previous year
        for(i in 1..12){
            calendar.add(java.util.Calendar.MONTH,-1)
            loadedDates.add(calendar.time)
        }
        // Sort by time
        latestPos += 12
        loadedDates.sort()
    }
    private fun loadNextMonths(){
        val calendar = java.util.Calendar.getInstance()
        calendar.time = loadedDates.last()

        // Load the next year
        for(i in 1..12){
            calendar.add(java.util.Calendar.MONTH,1)
            loadedDates.add(calendar.time)
        }
        // Sort by time
        loadedDates.sort()
    }
}
/*
//    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var listAdapter: CalendarAdapter
    private lateinit var binding: FragmentCalendarBinding

//    private lateinit var monthYearText: TextView
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
        binding.monthYearText.text = dateFormatter(selectedMonthDate)

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
        binding.calendarRecyclerView.scrollToPosition(listAdapter.getItemPos(selectedMonthDate))

        binding.calendarRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    // Get the position of the selected month
                    val pos = layoutManager.findFirstVisibleItemPosition()

                    // If switched to a different month
                    if(latestPos != pos){
                        // Assign new selected date
                        selectedMonthDate = listAdapter.getItemByPos(pos)
                        binding.monthYearText.text = dateFormatter(selectedMonthDate)
                        // Assign new latest position
                        latestPos = pos

                        // If at the start of the list, load previous months and scroll back to the same month
                        if(pos == 0){
                            loadPreviousMonths()
                            listAdapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(listAdapter.getItemPos(selectedMonthDate))
                        }
                        // If at the end of the list, load next months and scroll back to the same month
                        if(pos == loadedDates.size-1){
                            loadNextMonths()
                            listAdapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(listAdapter.getItemPos(selectedMonthDate))
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

    private fun dateFormatter(date : Date) : String {
        val sdf = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun loadPreviousMonths(){
        val calendar = java.util.Calendar.getInstance()
        calendar.time = loadedDates[0]

        // Load the previous year
        for(i in 1..12){
            calendar.add(java.util.Calendar.MONTH,-1)
            loadedDates.add(calendar.time)
        }
        // Sort by time
        latestPos += 12
        loadedDates.sort()
    }
    private fun loadNextMonths(){
        val calendar = java.util.Calendar.getInstance()
        calendar.time = loadedDates.last()

        // Load the next year
        for(i in 1..12){
            calendar.add(java.util.Calendar.MONTH,1)
            loadedDates.add(calendar.time)
        }
        // Sort by time
        loadedDates.sort()
    }

    private fun setupClickListener() {
        binding.previousMonth.setOnClickListener {
            previousMonthAction()
        }
        binding.nextMonth.setOnClickListener {
            nextMonthAction()
        }

        listAdapter.onDayClickListener = {
            Toast.makeText(context, "${it.time} clicked!!!", Toast.LENGTH_SHORT).show()
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