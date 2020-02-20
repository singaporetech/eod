package com.boliao.eod

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * TODO RECEIVERS 1: statically triggered receiver that sets off scheduled services
 * - WorkManagers for deferrable but guaranteed work
 * - AlarmManagers for exact timed jobs but no guarantee on network
 */
class OnBootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "OnBootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceived triggered")

        // check that action actually matches
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            /**
             * TODO SERVICES 3: create a reminder for user to charge phone periodically
             * - not to be confused, this is not detecting battery low
             * - this is simply reminding to charge battery
             * - remember: WorkManager cannot guarantees it will run, but not exact time
             */
            // a. build a set of constraints, e.g., network connected and enough batt
            val workConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(false)
                    .setRequiresDeviceIdle(false)
                    .setTriggerContentUpdateDelay(3, TimeUnit.SECONDS)
                    .build()

            // b. build a work request from a Worker.class that fires periodically with the constraints above
            // (note that periodic tasks cannot be < 15mins)
            val pwr = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
                    .setConstraints(workConstraints)
                    .build()

            // c. enqueue the work request with the WorkManager singleton
            WorkManager.getInstance(context).enqueue(pwr
        }
        rk
    }
}