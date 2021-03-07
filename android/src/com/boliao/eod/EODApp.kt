package com.boliao.eod

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Represents a non-UI global place to contain the global App Context.
 * Many things should not be owned individually in the components and should be here.
 *
 * Note on context:
 *  - load resources (strings, assets, etc)
 *  - starting Activities and Services
 *  - send broadcasts, register receivers
 *  - Activity context can inflate layouts
 */
class EODApp: Application() {
    // 1. lazy init the Room DB
    // 2. lazy init the player repo with the DAO from the DB
    private val db by lazy { PlayerDB.getDatabase(this) }
    val playerRepo by lazy { PlayerRepo(db.playerDAO())}

    // TODO NETWORKING 1.0: add the volley network request queue and weather repo here
    // 1. lazy init the volley network request queue using the same patten as repo
    private val networkRequestQueue by lazy { NetworkRequestQueue.getInstance(this) }
    val weatherRepo by lazy { WeatherRepo(networkRequestQueue)}


    // TODO SERVICES 8: create a reminder Worker for charging
    // 1. create a reminder Worker class (harness the power of ALT-ENTER)
    //    - create/send a notification in doWork() to remind the user to charge the phone
    //    - test adding an icon image asset from the "New" menu, as the small icon
    // 2. build a set of work constraints, e.g.,
    //    - connected to network
    //    - enough battery
    // 3. build a work request of the Worker class type,
    //    - fires periodically every 15 mins
    //    - set with the constraints above
    // 4. enqueue the work request with the WorkManager singleton
    // 5. edit the constraints on the device and observe the worker behavior
    //    - set device battery level using adb
    //    - set the airplane mode

    /**
     * Init the stuff that needs context to be initialized.
     */
    override fun onCreate() {
        super.onCreate()

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
