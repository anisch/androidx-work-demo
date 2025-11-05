package com.example.androidx_work_demo

import android.app.Application
import androidx.core.content.edit
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        cancelAppStatusWorker(1000)
        scheduleAppStatusWorker(1000)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        sharedPreferences.edit {
            putLong("last_version_code", 1000)
        }
    }
}
