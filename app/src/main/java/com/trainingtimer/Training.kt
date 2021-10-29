package com.trainingtimer

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Training(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    /*var sets: Int,
    var times: Int*/
)