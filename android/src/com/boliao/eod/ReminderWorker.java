package com.boliao.eod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
//import androidx.core.app.NotificationCompat;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ReminderWorker extends Worker {
    private static final String TAG = "ReminderWorker";
    private int count = 0;

//    private NotificationManagerCompat notMgr;
    private NotificationManager notMgr;
    private final int NID = 8;
    private final String NCID = "2";
    private final String NNAME = "My Channel Name";
    private final String NDESC = "This is testing channel";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        // init notification channel
        NotificationChannel chan = new NotificationChannel(NCID, NNAME, NotificationManager.IMPORTANCE_HIGH);
        chan.setDescription(NDESC);

        // init notification manager
        notMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notMgr.createNotificationChannel(chan);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "REMINDER TO CHARGE" + count++);

        // TODO notification to remind charge
        // - note the builder pattern is a common design pattern used for creating objs in the api
        Notification n = new NotificationCompat.Builder(super.getApplicationContext(), NCID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("eod REMINDER to charge")
                .setContentText("How to fight bugs if ur phone juice run out...")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{1000,1000,1000})
                .setAutoCancel(true)
                .build();

        notMgr.notify(NID, n);

        // indicate success or failure
        // - e.g., Result.retry() tells work manager to try again later
        // - e.g., Result.failure() tells WM not to try already
        return Result.success();
    }
}
