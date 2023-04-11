package com.trainingtimer

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.NumberPicker
import android.widget.Toast
import java.util.*

class TimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    FrameLayout(context, attrs, defStyle) {
    // state
    private var mCurrentMinutes = 0
    private var mCurrentSeconds = 0

    // ui components
    private val mMinutePicker: NumberPicker
    private val mSecondPicker: NumberPicker

    // callbacks
    private var mOnTimeChangedListener: OnTimeChangedListener? = null

    interface OnTimeChangedListener {
        fun onTimeChanged(view: TimePicker?, minute: Int, seconds: Int)
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(
            R.layout.time_picker_widget,
            this,
            true
        )

        // digits of minute
        mMinutePicker = findViewById<View>(R.id.minute) as NumberPicker
        mMinutePicker.minValue = 0
        mMinutePicker.maxValue = 59
        mMinutePicker.setFormatter(TWO_DIGIT_FORMATTER)
        mMinutePicker.setOnValueChangedListener { spinner, oldVal, newVal ->
            mCurrentMinutes = newVal
            onTimeChanged()
        }

        // digits of seconds
        mSecondPicker = findViewById<View>(R.id.seconds) as NumberPicker
        mSecondPicker.minValue = 0
        mSecondPicker.maxValue = 59
        mSecondPicker.setFormatter(TWO_DIGIT_FORMATTER)
        mSecondPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            mCurrentSeconds = newVal
            onTimeChanged()
        }

        // initialize to current time
        val cal = Calendar.getInstance()
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER)

        setCurrentMinute(cal[Calendar.MINUTE])
        setCurrentSecond(cal[Calendar.SECOND])
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mMinutePicker.isEnabled = enabled
    }

    private class SavedState : BaseSavedState {
        private val mHour: Int
        private val mMinute: Int

        constructor(superState: Parcelable?, hour: Int, minute: Int) : super(superState) {
            mHour = hour
            mMinute = minute
        }

        private constructor(i: Parcel) : super(i) {
            mHour = i.readInt()
            mMinute = i.readInt()
        }

        fun getMinute(): Int {
            return mMinute
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(mHour)
            dest.writeInt(mMinute)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(i: Parcel): SavedState {
                return SavedState(i)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, mCurrentMinutes, mCurrentSeconds)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        setCurrentMinute(ss.getMinute())
    }

    fun setOnTimeChangedListener(onTimeChangedListener: OnTimeChangedListener?) {
        mOnTimeChangedListener = onTimeChangedListener
    }

    fun getCurrentMinutes(): Int {
        return mCurrentMinutes
    }

    fun setCurrentMinute(currentMinute: Int) {
        mCurrentMinutes = currentMinute
        updateMinuteDisplay()
    }

    fun getCurrentSeconds(): Int {
        return mCurrentSeconds
    }

    fun setCurrentSecond(currentSecond: Int) {
        mCurrentSeconds = currentSecond
        updateSecondsDisplay()
    }

    override fun getBaseline(): Int {
        return mMinutePicker.baseline
    }

    private fun onTimeChanged() {
        mOnTimeChangedListener?.onTimeChanged(
            this,
            getCurrentMinutes(),
            getCurrentSeconds()
        ) ?: Toast.makeText(context, "onTimeChanged listener null", Toast.LENGTH_SHORT).show()
    }

    private fun updateMinuteDisplay() {
        mMinutePicker.value = mCurrentMinutes
        mOnTimeChangedListener?.onTimeChanged(
            this,
            getCurrentMinutes(),
            getCurrentSeconds()
        ) ?: Toast.makeText(context, "updateMinuteDisplay listener null", Toast.LENGTH_SHORT)
            .show()
    }

    private fun updateSecondsDisplay() {
        mSecondPicker.value = mCurrentSeconds
        mOnTimeChangedListener?.onTimeChanged(
            this,
            getCurrentMinutes(),
            getCurrentSeconds()
        ) ?: Toast.makeText(context, "updateSecondsDisplay listener null", Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        private val NO_OP_CHANGE_LISTENER: OnTimeChangedListener = object : OnTimeChangedListener {
            override fun onTimeChanged(
                view: TimePicker?,
                minute: Int,
                seconds: Int
            ) {
            }
        }
        val TWO_DIGIT_FORMATTER = NumberPicker.Formatter { value ->
            String.format("%02d", value)
        }
    }
}