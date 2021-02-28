package com.boliao.eod

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {

    init {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.createNotificationChannel(
                    NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            context.getString(R.string.channel_name),
                            NotificationManager.IMPORTANCE_HIGH
                    )
            )
    }

    override fun doWork(): Result {

        // build the notification
        val noti = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_batt)
                .setContentTitle("Exercise Or Die REMINDER")
                .setColor(Color.RED)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentText("pls go and charge your handphone lah deh")
                .setAutoCancel(true)
                .build()

        // activate the notification
        notificationManager.notify(NOTIFY_ID, noti)

        return Result.success()
    }

    companion object {
        private val TAG = ReminderWorker::class.simpleName

        // notification vars
        private const val NOTIFICATION_CHANNEL_ID = "EOD CHANNEL"
        private const val NOTIFY_ID = 818

        // - add var for NotificationManager
        private lateinit var notificationManager: NotificationManager
    }
}