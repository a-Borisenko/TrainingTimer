package com.trainingtimer.timerapp.utils

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters

class TimerWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        /*try {
            //
        } catch (e: Exception) {
            return Result.failure()
        }*/
        return Result.success()
    }

    /*fun notification(progress: String): Notification {
    val intent = WorkManager.getInstance(applicationContext)
        .createCancelPendingIntent(id)

    return NotificationCompat.Builder(applicationContext, id)
        .setContentTitle(title)
        .setContentText(progress)
        .addAction(android.R.drawable.ic_delete, cancel, intent)
        .build()
}

suspend fun download(
    inputUrl: String,
    outputFile: String,
    callback: suspend (progress: String) -> Unit
): Nothing = TODO()

fun createForegroundInfo(progress: String): ForegroundInfo {
    return ForegroundInfo(id.toString().toInt(), notification(progress))
}

override suspend fun getForegroundInfo(): ForegroundInfo {
    return super.getForegroundInfo()
}

override suspend fun doWork(): Result {
    download(inputUrl, outputFile) { progress ->
        val progress = "Progress $progress %"
        setForeground(createForegroundInfo(progress))
    }
    return Result.success()
}*/
}