package com.trainingtimer.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Training(
    val sets: Int,
    val title: String,
    val times: String,
    val rest: String,
    @PrimaryKey var id: Int = UNDEFINED_ID
) {
    companion object {

        const val UNDEFINED_ID = -1
        const val START_TIME = 0L
    }
}