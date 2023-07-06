package com.trainingtimer.domain

import androidx.room.Entity
import java.util.UUID

@Entity(
    tableName = "training"
)
data class Training(
    val sets: Int,
    val title: String,
    val times: String,
    val rest: String,
    var id: Int = UUID.randomUUID().toString().toInt()
) {
    companion object {

        const val UNDEFINED_ID = -1
    }
}