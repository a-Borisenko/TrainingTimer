package com.trainingtimer.utils

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.trainingtimer.domain.entity.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimerWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    private val startTime = workerParams.inputData.getLong(TIME, 0L)

    override fun doWork(): Result {
        isCounting = true
        while (secRemain > 0L) {
            Thread.sleep(1000)
            val progress = (secRemain-- * 100f) / startTime
            _timerStateFlow.update { it.copy(secRemain = secRemain, progress = progress) }
            Log.d("TimerWorker", "sec = $secRemain; progress = $progress")
        }
        isCounting = false
        return Result.success()
    }

    companion object {

        var secRemain = 0L
        var isCounting = false

        private val _timerStateFlow =
            MutableStateFlow(TimerState(secRemain, 100f))
        val timerStateFlow: StateFlow<TimerState> = _timerStateFlow.asStateFlow()

        private const val TIME = "time"
        const val WORK_NAME = "work name"

        fun makeRequest(time: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<TimerWorker>()
                .setInputData(workDataOf(TIME to time))
                .build()
        }
    }
}