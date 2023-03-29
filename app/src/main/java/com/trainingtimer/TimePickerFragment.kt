package com.trainingtimer

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val minute = c.get(Calendar.MINUTE)
        val seconds = c.get(Calendar.SECOND)

        return TimePickerDialog(context, this, minute, seconds, true)
    }

    override fun onTimeSet(view: TimePicker?, minute: Int, seconds: Int) {
        val result = minute.toLong() * 60 + seconds.toLong()
        setFragmentResult("key", bundleOf("time" to result))
    }
}