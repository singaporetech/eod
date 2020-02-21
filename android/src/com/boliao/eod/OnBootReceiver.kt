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
 * TODO RECEIVERS 1: build a statically triggered receiver that sets off scheduled services
 * - derive from BroadcastReceiver
 * - implement onReceive to handle response when BOOT_COMPLETED intent.action received
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
             * TODO SERVICES 13: create a reminder for user to charge phone periodically
             * WorkManagers are for deferrable but guaranteed work. If you need exact timed jobs
             * use AlarmManagers (but still no guarantee on network).
             * Not to be confused, this is not detecting battery low it is simply reminding to charge.
             */
            // - build a set of work constraints, e.g., network connected / not idle
            // - set update delay of 3 secs

            // - create a ReminderWorker class from Worker,
            // - build a work request of type ReminderWorker that fires periodically with constraints above
            //   (note that periodic tasks cannot be < 15mins)

            // - enqueue the work request with the WorkManager singleton
        }
    }
}