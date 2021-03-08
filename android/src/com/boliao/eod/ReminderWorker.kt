package com.boliao.eod

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {

    private val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun doWork(): Result {
        // create a notification to remind the user to do something

        // build the notification
        val noti = Notification.Builder(applicationContext, GameStateService.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_walk)
                .setContentTitle("EOD Reminder")
                .setColor(Color.CYAN)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentText("Pls stand up and walk!")
                .setAutoCancel(true)
                .build()

        // activate the notification
        notificationManager.notify(GameStateService.NOTIFY_ID, noti)

        return Result.success()
    }
}