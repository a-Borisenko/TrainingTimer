package com.trainingtimer

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

class PrefUtil {

    companion object{

        fun getTimerLength(context: TrainingFragment): Int {
            //placeholder
            return 1
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID =
            "com.timer.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: TrainingFragment): Long {
            val preferences = FragmentManager.findFragment<TrainingFragment>(View(context: Context))
                //FragmentActivity.getSupportFragmentManager
                //PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: TrainingFragment) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.timer.timer_state"

        fun getTimerState(context: TrainingFragment): TrainingFragment.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TrainingFragment.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TrainingFragment.TimerState, context: TrainingFragment) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "com.timer.seconds_remaining"

        fun getSecondsRemaining(context: TrainingFragment): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: TrainingFragment) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }
    }
}