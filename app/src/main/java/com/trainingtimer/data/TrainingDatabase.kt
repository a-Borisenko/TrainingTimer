package com.trainingtimer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trainingtimer.domain.Training
import java.util.concurrent.Executors

@Database(version = 1, entities = [Training::class])
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    companion object {

        @Volatile private var INSTANCE: TrainingDatabase? = null

        fun getInstance(context: Context): TrainingDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                TrainingDatabase::class.java, "Sample.db")
                // prepopulate the database after onCreate was called
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // insert the data on the IO Thread
                        ioThread {
                            getInstance(context).trainingDao().insertData(PREPOPULATE_DATA)
                        }
                    }
                })
                .build()

        val PREPOPULATE_DATA = listOf(
            Training(1, "подтягивания", "x5", "01:00"),
            Training(1, "отжимания", "x10", "01:00"),
            Training(1, "приседания", "x15", "01:00")
        )

        fun ioThread(f: () -> Unit) {
            Executors.newSingleThreadExecutor().execute(f)
        }
    }
}