package com.boliao.eod

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class EODApp: Application() {
    // 1. lazy init the Room DB
    // 2. lazy init the player repo with the DAO from the DB
    // This should be done at the application level in
    val db by lazy { PlayerDB.getDatabase(this) }
    val repo by lazy { PlayerRepo(db.playerDAO())}

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
}