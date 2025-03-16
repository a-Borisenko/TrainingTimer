package com.trainingtimer.presentation.details

data class TrainingState(
    val sets: String = "",
    val title: String = "",
    val times: String = "",
    val secRemain: String = "",
    val progress: String = "",
    val errorInputSets: Boolean = false,
    val errorInputTitle: Boolean = false,
    val errorInputTimes: Boolean = false,
    val shouldCloseScreen: Boolean = false
)
