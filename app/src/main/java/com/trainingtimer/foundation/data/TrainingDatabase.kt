package com.trainingtimer.foundation.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trainingtimer.foundation.domain.Training

@Database(version = 1, entities = [Training::class])
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    /*companion object {

        @Volatile
        private var INSTANCE: TrainingDatabase? = null

        fun getInstance(context: Context): TrainingDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: database(context).also { INSTANCE = it }
            }

        fun database(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TrainingDatabase::class.java,
            "training-database"
        ).addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                ioThread {
                    with(getInstance(context).trainingDao()) {
                        addTraining(Training(1, "подтягивания", "x5", "01:00"))
//                        addTraining(Training(1, "отжимания", "x10", "01:00"))
//                        addTraining(Training(1, "приседания", "x15", "01:00"))
//                        for (i in 4 until 100) {
//                            val item = Training(i, "Training №$i", "x$i", "01:00")
//                            addTraining(item)
//                        }
                    }
                    //update list of trainings
                }
            }
        })
            .build()

        fun ioThread(f: () -> Unit) {
            Executors.newSingleThreadExecutor().execute(f)
        }
    }*/
}