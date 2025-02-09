package com.trainingtimer.domain.entity

import java.util.Date

data class ExerciseTime(
    val id: Int,
    val date: Date,
    val title: String,
    val time: String
)
