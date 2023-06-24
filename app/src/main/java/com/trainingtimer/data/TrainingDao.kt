package com.trainingtimer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trainingtimer.domain.Training

@Dao
interface TrainingDao {

    @Query("SELECT * FROM trainings")
    fun getTrainings(): LiveData<List<Training>>

    @Query("SELECT * FROM trainings WHERE trainingId = (:trainingId)")
    fun getTraining(trainingId: Int): LiveData<Training?>

    @Update
    fun updateTraining(training: Training)

    @Insert
    fun addTraining(training: Training)

    @Insert
    fun deleteTraining(training: Training)
}