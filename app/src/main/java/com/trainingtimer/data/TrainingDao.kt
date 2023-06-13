package com.trainingtimer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.trainingtimer.domain.Training

@Dao
interface TrainingDao {

    @Query("SELECT * FROM trainings")
    fun getTrainings(): LiveData<List<Training>>

    @Query("SELECT * FROM trainings WHERE trainingId = (:trainingId)")
    fun getTraining(trainingId: Int): Training
}