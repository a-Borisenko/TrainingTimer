package com.trainingtimer.presentation.details

data class TrainingState(
    val sets: String = "",
    val title: String = "",
    val times: String = "",
    val secRemain: String = "",
    val progress: Int = 0,
    val errorInputSets: Boolean = false,
    val errorInputTitle: Boolean = false,
    val errorInputTimes: Boolean = false,
    val shouldCloseScreen: Boolean = false
)
