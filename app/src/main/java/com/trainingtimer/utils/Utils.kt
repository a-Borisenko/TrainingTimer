package com.trainingtimer.utils

import android.content.Context
import android.widget.Toast
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

fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
    if (dateFirst == null || dateSecond == null) return false
    return sdf(dateFirst) == sdf(dateSecond)
}


fun Context.toast(message: String?) {
    message?.let {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}