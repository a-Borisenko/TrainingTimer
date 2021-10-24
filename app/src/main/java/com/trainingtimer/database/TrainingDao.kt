package com.trainingtimer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trainingtimer.Training
import java.util.*

@Dao
interface TrainingDao {

    @Query("SELECT * FROM training")
    //fun getTrainings(): List<Training>
    fun getTrainings(): LiveData<List<Training>>

    @Query("SELECT * FROM training WHERE id = (:id)")
    //fun getTraining(id: UUID): Training?
    fun getTraining(id: UUID): LiveData<Training?>

    @Update
    fun updateTraining(training: Training)

    @Insert
    fun addTraining(training: Training)
}