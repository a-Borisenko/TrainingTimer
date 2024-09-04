package com.trainingtimer.views.timepicker

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.NumberPicker
import com.trainingtimer.R

class TimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    FrameLayout(context, attrs, defStyle) {
    // state
    private var currentMinutes = 0
    private var currentSeconds = 0

    // ui components
    private val minutePicker: NumberPicker
    private val secondPicker: NumberPicker

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(
            R.layout.time_picker_widget,
            this,
            true
        )
        // digits of minute
        minutePicker = findViewById<View>(R.id.minute) as NumberPicker
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        setCurrentMinute(0)
        minutePicker.setFormatter(TWO_DIGIT_FORMATTER)
        minutePicker.setOnValueChangedListener { spinner, oldVal, newVal ->
            currentMinutes = newVal
        }

        // digits of seconds
        secondPicker = findViewById<View>(R.id.seconds) as NumberPicker
        secondPicker.minValue = 0
        secondPicker.maxValue = 59
        setCurrentSecond(0)
        secondPicker.setFormatter(TWO_DIGIT_FORMATTER)
        secondPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            currentSeconds = newVal
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        minutePicker.isEnabled = enabled
    }

    override fun getBaseline(): Int {
        return minutePicker.baseline
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return TimePickerSavedState(superState, currentMinutes, currentSeconds)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as TimePickerSavedState
        super.onRestoreInstanceState(ss.superState)
        setCurrentMinute(ss.minutes)
        setCurrentSecond(ss.seconds)
    }

    fun getCurrentMinutes(): Int {
        return currentMinutes
    }

    private fun setCurrentMinute(currentMinute: Int) {
        currentMinutes = currentMinute
    }

    fun getCurrentSeconds(): Int {
        return currentSeconds
    }

    private fun setCurrentSecond(currentSecond: Int) {
        currentSeconds = currentSecond
    }

    companion object {
        val TWO_DIGIT_FORMATTER = NumberPicker.Formatter { value -> String.format("%02d", value) }
    }
}