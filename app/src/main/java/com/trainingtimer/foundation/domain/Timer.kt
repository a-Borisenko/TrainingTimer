package com.trainingtimer.foundation.domain

import android.os.CountDownTimer
import android.util.Log
import com.trainingtimer.timerapp.views.details.TrainingFragment
import com.trainingtimer.timerapp.views.details.TrainingViewModel

object Timer : CountDownTimer(TrainingFragment.secondsRemaining * 1000, 1000) {
    override fun onFinish() {
        Log.d("new Timer", "done!!!")
    }

    override fun onTick(millisUntilFinished: Long) {
        TrainingFragment.secondsRemaining = millisUntilFinished / 1000
        Log.d("new Timer", TrainingFragment.secondsRemaining.toString())
    }
}