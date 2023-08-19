package com.trainingtimer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trainingtimer.domain.Training

@Dao
interface TrainingDao {
    //training.value = null

    @Query("SELECT * FROM training")
    fun getTrainings(): LiveData<List<Training>>

    @Query("SELECT * FROM training WHERE id = (:trainingId)")
    fun getTraining(trainingId: Int): LiveData<Training?>

    @Query("SELECT id FROM training")
    fun getCount(): List<Int>

    /*@Query("SELECT * FROM training WHERE id IN (:id)")
    fun loadAllByIds(id: IntArray): List<Training>*/

    @Update
    fun updateTraining(training: Training)

    @Insert
    fun addTraining(training: Training)

    @Delete
    fun deleteTraining(training: Training)

    /*@Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
           "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)*/
}