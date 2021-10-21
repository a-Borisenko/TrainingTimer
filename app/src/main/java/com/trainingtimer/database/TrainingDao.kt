package com.trainingtimer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.trainingtimer.Training
import java.util.*

@Dao
interface TrainingDao {

    @Query("SELECT * FROM training")
    //fun getTrainings(): List<Training>
    fun getTrainings(): LiveData<List<Training>>

    @Query("SELECT * FROM training WHERE trainingId = (:id)")
    //fun getTraining(id: UUID): Training?
    fun getTraining(id: UUID): LiveData<Training?>
}