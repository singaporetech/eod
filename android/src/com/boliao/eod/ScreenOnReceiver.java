package com.boliao.eod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ScreenOnReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenOnReceiver";
    public static final int PI_CODE = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "com.boliao.eod.channel";
    public static final int NOTIFY_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i(TAG, "in BR");

       // init notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // only from Android Oreo, now need to set notification channels before it will appear
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "just another channel name",
                    NotificationManager.IMPORTANCE_HIGH
            ));
        }

        // - can get data from intent if we need
        Intent openAppIntent = new Intent(context, AndroidLauncher.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PI_CODE, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("O.M.G. you turned on the screen, pls go to my app now!!!")
                .setAutoCancel(true)
                .build();

        notificationManager.notify(NOTIFY_ID, notification);
    }
}
