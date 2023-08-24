package com.trainingtimer.foundation.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.trainingtimer.foundation.domain.Training
import com.trainingtimer.foundation.domain.TrainingRepository
import java.util.concurrent.Executors

private const val DATABASE_NAME = "training-database"

class TrainingRepositoryImpl private constructor(context: Context) : TrainingRepository {

    private val database: TrainingDatabase = Room.databaseBuilder(
        context.applicationContext,
        TrainingDatabase::class.java,
        DATABASE_NAME
    )
//        .createFromAsset("database/myapp.db")
//        .createFromFile(File("mypath"))
        /*.addCallback(object : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            ioThread {
                with(TrainingDatabase.getInstance(context).trainingDao()) {
                    addTraining(Training(1, "подтягивания", "x5", "01:00"))
                    addTraining(Training(1, "отжимания", "x10", "01:00"))
                    addTraining(Training(1, "приседания", "x15", "01:00"))
                    for (i in 4 until 100) {
                        val item = Training(i, "Training №$i", "x$i", "01:00")
                        addTraining(item)
                    }
                }
            }
        }
    })*/
        .build()

    private val trainingDao = database.trainingDao()  //TrainingDatabase.database(context).trainingDao()
    private val executor = Executors.newSingleThreadExecutor()

    private var autoIncrementId = 0

//    var a = trainingDao.getTrainings().value?.size ?: 100

    init {
        //TODO: init must be only if there is no DataBase on the phone yet
//        addTraining(Training(1, "подтягивания", "x5", "01:00"))
//        addTraining(Training(1, "отжимания", "x10", "01:00"))
//        addTraining(Training(1, "приседания", "x15", "01:00"))
        /*for (i in 4 until 100) {
            val item = Training(i, "Training №$i", "x$i", "01:00")
            addTraining(item)
        }*/
    }

    override fun addTraining(training: Training) {
        if (autoIncrementId == 0) {
            /*trainingDao.getTrainings().observe(LifecycleOwner) {
                autoIncrementId = it.size
            }*/
            autoIncrementId = 102     //get number of trainings in DataBase
        }
        if (training.id == Training.UNDEFINED_ID) {
            Log.d("RepositoryImpl", "autoIncrementId = $autoIncrementId")
            training.id = autoIncrementId++
        }
        executor.execute {
            trainingDao.addTraining(training)
        }
    }

    override fun deleteTraining(training: Training) {
        executor.execute {
            trainingDao.deleteTraining(training)
        }
    }

    override fun editTraining(training: Training) {
        executor.execute {
            val oldElement = getTraining(training.id).value
                ?: throw IllegalStateException("Training with id${training.id} not found")
            trainingDao.deleteTraining(oldElement)
            trainingDao.addTraining(training)
        }
    }

    override fun getTraining(trainingId: Int): LiveData<Training?> {
        return trainingDao.getTraining(trainingId)
    }

    override fun getTrainingList(): LiveData<List<Training>> {
        return trainingDao.getTrainings()
    }

    private fun updateTraining(training: Training) {
        executor.execute {
            trainingDao.updateTraining(training)
        }
    }

    companion object {
        private var INSTANCE: TrainingRepositoryImpl? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TrainingRepositoryImpl(context)
            }
        }

        fun get(): TrainingRepositoryImpl {
            return INSTANCE
                ?: throw IllegalStateException("TrainingRepositoryImpl must be initialized")
        }
    }
}