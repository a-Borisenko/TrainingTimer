package com.trainingtimer.domain.entity

import java.util.Date

sealed class ExerciseState {

    object Loading : ExerciseState()
    object Error : ExerciseState()

    data class ExerciseReps(
        val id: Int,
        val date: Date,
        val title: String,
        val reps: String
    ) : ExerciseState()
    
    data class ExerciseTime(
        val id: Int,
        val date: Date,
        val title: String,
        val time: String
    ) : ExerciseState()
}
