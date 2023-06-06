package com.trainingtimer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trainingtimer.domain.Training

@Entity(
    tableName = "trainings"
)
data class TrainingDbEntity(
    val sets: Int,
    val title: String,
    val times: String,
    val rest: String,
    @PrimaryKey(autoGenerate = true) var id: Int
) {

    fun toTraining(): Training = Training(
        sets = sets,
        title = title,
        times = times,
        rest = rest,
        id = id
    )
}
