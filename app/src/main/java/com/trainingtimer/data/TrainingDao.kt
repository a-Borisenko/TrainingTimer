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
    //There is a problem with the query: [SQLITE_ERROR] SQL error or missing database (no such table: training)

    //The columns returned by the query does not have the fields [id] in com.trainingtimer.domain.Training
    //even though they are annotated as non-null or primitive. Columns returned by the query: [sets,title,times,rest,trainingId]
    //public abstract androidx.lifecycle.LiveData<java.util.List<com.trainingtimer.domain.Training>> getTrainings();

    @Query("SELECT * FROM training")
    fun getTrainings(): LiveData<List<Training>>

    @Query("SELECT * FROM training WHERE id = (:id)")
    fun getTraining(id: Int): LiveData<Training?>

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