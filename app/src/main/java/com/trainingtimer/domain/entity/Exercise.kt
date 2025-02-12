package com.trainingtimer.domain.entity

import java.util.Date

data class Exercise(
    val id: Int,
    val date: Date,
    val title: String,
    val type: ExerciseType,
    val details: String
)
