package com.trainingtimer.domain.entity

sealed class Exercise/*(
    val id: Int,
    val date: Date,
    val title: String,
    val type: ExerciseType,
    val details: String
    // move to dataList
)*/

object Initial : Exercise()
object Loading : Exercise()

data class ExerciseDesc(val dataList: List<String>) : Exercise()
