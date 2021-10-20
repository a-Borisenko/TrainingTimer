package com.trainingtimer.database

import androidx.room.Dao
import androidx.room.Query
import com.trainingtimer.Training
import java.util.*

@Dao
interface TrainingDao {
    @Query("SELECT * FROM training")
    fun getTrainings(): List<Training>
    @Query("SELECT * FROM training WHERE trainingId = (:id)")
    fun getTraining(id: UUID): Training?
}