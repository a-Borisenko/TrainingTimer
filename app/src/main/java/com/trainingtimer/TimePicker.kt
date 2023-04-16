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
        setCurrentMinute(0)
        mMinutePicker.setFormatter(TWO_DIGIT_FORMATTER)
        mMinutePicker.setOnValueChangedListener { spinner, oldVal, newVal ->
            mCurrentMinutes = newVal
        }

        // digits of seconds
        mSecondPicker = findViewById<View>(R.id.seconds) as NumberPicker
        mSecondPicker.minValue = 0
        mSecondPicker.maxValue = 59
        setCurrentSecond(0)
        mSecondPicker.setFormatter(TWO_DIGIT_FORMATTER)
        mSecondPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            mCurrentSeconds = newVal
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mMinutePicker.isEnabled = enabled
    }

    override fun getBaseline(): Int {
        return mMinutePicker.baseline
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

    fun getCurrentMinutes(): Int {
        return mCurrentMinutes
    }

    fun setCurrentMinute(currentMinute: Int) {
        mCurrentMinutes = currentMinute
    }

    fun getCurrentSeconds(): Int {
        return mCurrentSeconds
    }

    fun setCurrentSecond(currentSecond: Int) {
        mCurrentSeconds = currentSecond
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

    companion object {
        val TWO_DIGIT_FORMATTER = NumberPicker.Formatter { value -> String.format("%02d", value) }
    }
}