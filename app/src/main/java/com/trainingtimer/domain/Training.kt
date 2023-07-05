package com.trainingtimer.domain

import androidx.room.PrimaryKey

data class Training(
    val sets: Int,
    val title: String,
    val times: String,
    val rest: String,
    @PrimaryKey var id: Int = UNDEFINED_ID
) {
    companion object {

        const val UNDEFINED_ID = -1
    }
}