package com.example.androidx_work_demo

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.toJavaDuration

@HiltWorker
class AppStatusWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences("retry_prefs", Context.MODE_PRIVATE)

    override suspend fun doWork(): Result {

        val workRequestId = inputData.getString("work_request_id") ?: "" //Get id from input data
        val retryCount = sharedPreferences.getInt(workRequestId, 0)

        return try {
            repeat(10) {
                delay(1000)
            }
            Result.success()
        } catch (e: Exception) {
            if (retryCount < 5) {
                sharedPreferences.edit { putInt(workRequestId, retryCount+1) }
                Result.retry()
            } else {
                // ignore
                sharedPreferences.edit { remove(workRequestId) }
                Result.success()
            }
        }
    }
}

fun Context.scheduleAppStatusWorker(currentVersionCode: Long) {
    Timber.tag("AppStatusWorker").d("Scheduling AppStatusWorker")

    val currentTimeZone = TimeZone.currentSystemDefault()
    val currentInstant = Clock.System.now()

    val desiredTime = LocalTime(12, 0)

    val currentLocalDateTime = currentInstant.toLocalDateTime(currentTimeZone)
    Timber.tag("AppStatusWorker").v(currentTimeZone.toString())

    val targetLocalDateTime = LocalDate(
        year = currentLocalDateTime.year,
        month = currentLocalDateTime.month,
        day = currentLocalDateTime.day,
    ).atTime(desiredTime).toInstant(currentTimeZone)

    val targetInstant = if (targetLocalDateTime < currentInstant) {
        targetLocalDateTime.plus(1, DateTimeUnit.DAY, currentTimeZone)
    } else {
        targetLocalDateTime
    }

    val initialDelayDuration = targetInstant - currentInstant
    val initialDelayMinutes = initialDelayDuration.toJavaDuration().toMinutes()
    Timber.tag("AppStatusWorker").v("start AppStatusWorker in $initialDelayMinutes min")

    val constraints = Constraints.Builder()
        // Only run if there's a network connection
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workTag = "AppStatusWorker_$currentVersionCode"
    val periodicWorkRequest = PeriodicWorkRequestBuilder<AppStatusWorker>(
        1, TimeUnit.DAYS, // Daily Interval
        15, TimeUnit.MINUTES, // Flex time interval
    )
        .setConstraints(constraints)
        .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES) // Initial delay
        .setBackoffCriteria(
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 5,
            timeUnit = TimeUnit.MINUTES,
        )
        .setInputData(
            Data.Builder()
                .putString("work_request_id", workTag)
                .build()
        )
        .addTag(workTag)
        .build()

    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        "AppStatusWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest,
    )
}

fun Context.cancelAppStatusWorker(oldVersionCode: Long) {
    if (oldVersionCode != -1L) {
        val workManager = WorkManager.getInstance(this)
        val workTag = "AppStatusWorker_$oldVersionCode"
        workManager.cancelAllWorkByTag(workTag)
    }
}

