package com.trainingtimer.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun timeLongToString(time: Long): String {
    val min = time / 60
    val sec = time % 60
    return "${"%02d".format(min)}:${"%02d".format(sec)}"
}


val sdf: (Date) -> String = { date ->
    val calendar = Calendar.getInstance()
    calendar.time = date
    val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR).toString()

    val sdf = SimpleDateFormat("$weekNumber/EEE/dd/MM/yyyy", Locale.getDefault())
    sdf.format(date)
}

fun weekNumber(date: Date): String {
    return sdf(date).split("/")[0]
}

fun dayOfWeek(day: Int): String {
    return when (day) {
        0 -> "Monday"
        1 -> "Tuesday"
        2 -> "Wednesday"
        3 -> "Thursday"
        4 -> "Friday"
        5 -> "Saturday"
        6 -> "Sunday"
        else -> throw RuntimeException("day of week calculation mistake")
    }
}

fun dayOfMonth(date: Date): String {
    return sdf(date).split("/")[2]
}


fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
    if (dateFirst == null || dateSecond == null) return false
    return sdf(dateFirst) == sdf(dateSecond)
}


fun EditText.onChange(textChanged: ((String) -> Unit)) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textChanged(s.toString())
        }
    })
}


fun View.visible() {
    this.isVisible = true
}

fun View.gone() {
    this.isVisible = false
}


fun Context.toast(message: String?) {
    message?.let {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}