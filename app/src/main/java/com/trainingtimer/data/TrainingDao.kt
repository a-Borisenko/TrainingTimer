package com.trainingtimer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trainingtimer.domain.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM training")
    fun getTrainings(): Flow<List<Training>>

    @Query("SELECT * FROM training WHERE id = (:trainingId)")
    fun getTraining(trainingId: Int): Flow<Training?>

    @Update
    fun updateTraining(training: Training)

    @Insert
    fun addTraining(training: Training)

    @Delete
    fun deleteTraining(training: Training)

}