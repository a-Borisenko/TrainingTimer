package com.trainingtimer.views.timepicker

import android.os.Parcel
import android.os.Parcelable
import android.view.View.BaseSavedState

class TimePickerSavedState : BaseSavedState {
    val minutes: Int
    val seconds: Int

    constructor(superState: Parcelable?, minutes: Int, seconds: Int) : super(superState) {
        this.minutes = minutes
        this.seconds = seconds
    }

    private constructor(parcel: Parcel) : super(parcel) {
        minutes = parcel.readInt()
        seconds = parcel.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(minutes)
        dest.writeInt(seconds)
    }

    companion object CREATOR : Parcelable.Creator<TimePickerSavedState> {
        override fun createFromParcel(parcel: Parcel): TimePickerSavedState {
            return TimePickerSavedState(parcel)
        }

        override fun newArray(size: Int): Array<TimePickerSavedState?> {
            return arrayOfNulls(size)
        }
    }
}