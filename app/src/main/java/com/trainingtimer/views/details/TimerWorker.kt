package com.trainingtimer.views.details

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.trainingtimer.domain.Training
import com.trainingtimer.utils.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerWorker(
    context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    private var startTime = 0L
    private var progress = 100f

    override fun doWork(): Result {
        startTime = workerParameters.inputData.getLong(SEC_REMAIN, 0L)
        secRemain = startTime
        DataService.isCounting = true
        while (secRemain > 0L) {
            Thread.sleep(1000)
            _secRemainFlow.value = --secRemain
            progress = (secRemain.toFloat() * 100f) / startTime.toFloat()
            _progressFlow.value = progress
            Log.d("worker timer", "sec = $secRemain; progr = $progress")
//            emit(secRemain)
        }
        DataService.isCounting = false
        return Result.success()
    }

    companion object {

        const val SEC_REMAIN = "secRemain"
        const val WORK_NAME = "work"

        var isCounting = DataService.isCounting
        var isLast = true
        var secInit = 0L
        var secRemain = 0L
        var progressInit = 0f
        var currentId = Training.UNDEFINED_ID

        private val _secRemainFlow = MutableStateFlow(TimerService.secInit)
        val secRemainFlow: StateFlow<Long> = _secRemainFlow.asStateFlow()

        private val _progressFlow = MutableStateFlow(TimerService.progressInit)
        val progressFlow: StateFlow<Float> = _progressFlow.asStateFlow()

        fun makeRequest(sec: Long): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<TimerWorker>()
                .setInputData(workDataOf(SEC_REMAIN to sec))
                .build()
        }
    }
}