package com.boliao.eod

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.util.concurrent.TimeUnit

class EODApp: Application() {
    // 1. lazy init the Room DB
    // 2. lazy init the player repo with the DAO from the DB
    // This should be done at the application level in
    val db by lazy { PlayerDB.getDatabase(this) }
    val repo by lazy { PlayerRepo(db.playerDAO())}

    override fun onCreate() {
        super.onCreate()
        // TODO SERVICES 8: create a reminder Worker for charging
        // 1. build a set of work constraints, e.g.,
        //    - connected to network
        //    - enough battery
        // 2. build a work request of the Worker class type,
        //    - fires periodically every 15 mins
        //    - set with the constraints above
        // 3. enqueue the work request with the WorkManager singleton
        // 4. edit the constraints on the device and observe the worker behavior
        //    - set device battery level using adb
        //    - set the airplane mode

        val workConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build()

        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }
}