package com.trainingtimer.views.calendar

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentCalendarBinding
import com.trainingtimer.utils.DataService
import java.text.DateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var listAdapter: CalendarAdapter
    private lateinit var binding: FragmentCalendarBinding

    private lateinit var monthYearText: TextView
//    private val calendarRecyclerView: RecyclerView? = null
    private lateinit var selectedDate: LocalDate

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


        /*val today = Calendar.getInstance()

        binding.calendarRecyclerView.date = today.timeInMillis

        binding.calendarRecyclerView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Обработка выбранной даты
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            Toast.makeText(context, "date $dayOfMonth.$month.$year", Toast.LENGTH_SHORT).show()
            // Выполните необходимые действия с выбранной датой
        }*/
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