package com.trainingtimer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trainingtimer.domain.Training

@Dao
interface TrainingDao {
    //There is a problem with the query: [SQLITE_ERROR] SQL error or missing database (no such table: training)
    //The columns returned by the query does not have the fields [id] in com.trainingtimer.domain.Training even though they are annotated as non-null or primitive. Columns returned by the query: [sets,title,times,rest,trainingId]
    //public abstract androidx.lifecycle.LiveData<java.util.List<com.trainingtimer.domain.Training>> getTrainings();

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