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
    fun getTrainings(): LiveData<List<Training>>    //java compiling problem

    @Query("SELECT * FROM trainings WHERE trainingId = (:trainingId)")
    fun getTraining(trainingId: Int): LiveData<Training?>   //problem too

    @Update
    fun updateTraining(training: Training)    //same problem

    @Insert
    fun addTraining(training: Training)    //and here

    @Insert
    fun deleteTraining(training: Training)    //here too
}