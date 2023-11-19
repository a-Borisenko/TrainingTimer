package com.trainingtimer.timerapp.views.details

class TrainingUtils {

    companion object {
        fun timeStringToLong(time: String): Long {
            val min = (time.split(":"))[0].toLong()
            val sec = (time.split(":"))[1].toLong()
            return (min * 60 + sec)
        }

        fun timeLongToString(time: Long): String {
            val min = time / 60
            val sec = time % 60
            return "${"%02d".format(min)}:${"%02d".format(sec)}"
        }
    }
}