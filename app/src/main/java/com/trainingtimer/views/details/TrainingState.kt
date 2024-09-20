package com.trainingtimer.views.details

data class TrainingState(
    val sets: String = "",
    val title: String = "",
    val times: String = "",
    val secRemain: Long = TimerService.secInit,
    val progress: Float = 0f,
    val errorInputSets: Boolean = false,
    val errorInputTitle: Boolean = false,
    val errorInputTimes: Boolean = false,
    val shouldCloseScreen: Boolean = false
)
