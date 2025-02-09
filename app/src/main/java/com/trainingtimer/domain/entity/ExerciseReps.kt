package com.trainingtimer.domain.entity

import java.util.Date

data class ExerciseReps(
    val id: Int,
    val date: Date,
    val title: String,
    val reps: String
)
