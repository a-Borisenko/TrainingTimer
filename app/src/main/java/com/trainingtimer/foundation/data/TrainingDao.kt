package com.trainingtimer.foundation.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trainingtimer.foundation.domain.Training

@Dao
interface TrainingDao {

    @Query("SELECT * FROM training")
    fun getTrainings(): LiveData<List<Training>>

    @Query("SELECT * FROM training WHERE id = (:trainingId)")
    fun getTraining(trainingId: Int): LiveData<Training?>

    @Update
    fun updateTraining(training: Training)

    @Insert
    fun addTraining(training: Training)

    @Delete
    fun deleteTraining(training: Training)

}