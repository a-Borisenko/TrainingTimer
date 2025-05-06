package com.trainingtimer.presentation.details

data class TrainingState(
    val sets: String = "",
    val title: String = "",
    val times: String = "",
    val secRemain: String = "00:00",
    val progress: Int = 0,
    val errorSets: Boolean = false,
    val errorTitle: Boolean = false,
    val errorTimes: Boolean = false,
    val isCounting: Boolean = false,
    val shouldCloseScreen: Boolean = false
)
