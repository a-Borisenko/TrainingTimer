package com.trainingtimer.views.timepicker

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
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
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.time_picker_widget, this, true)

        minutePicker = createPicker(R.id.minute, ::onMinuteChanged)
        secondPicker = createPicker(R.id.seconds, ::onSecondsChanged)
    }

    private fun createPicker(pickerId: Int, listener: (Int) -> Unit): NumberPicker {
        return findViewById<NumberPicker>(pickerId).apply {
            minValue = 0
            maxValue = 59
            setFormatter(TWO_DIGIT_FORMATTER)
            setOnValueChangedListener { _, _, newVal -> listener(newVal) }
            value = 0 // setting start value
        }
    }

    private fun onMinuteChanged(newVal: Int) {
        currentMinutes = newVal
    }

    private fun onSecondsChanged(newVal: Int) {
        currentSeconds = newVal
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        minutePicker.isEnabled = enabled
        secondPicker.isEnabled = enabled
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
        currentMinutes = ss.minutes
        currentSeconds = ss.seconds
        minutePicker.value = currentMinutes
        secondPicker.value = currentSeconds
    }

    fun getCurrentMinutes(): Int {
        return currentMinutes
    }

    fun getCurrentSeconds(): Int {
        return currentSeconds
    }

    companion object {
        val TWO_DIGIT_FORMATTER = NumberPicker.Formatter { value -> String.format("%02d", value) }
    }
}