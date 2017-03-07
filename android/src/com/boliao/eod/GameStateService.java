package com.boliao.eod;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.opengl.Visibility;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.badlogic.gdx.utils.Timer;

import java.util.List;

import static android.app.Notification.DEFAULT_VIBRATE;
import static android.app.Notification.VISIBILITY_PUBLIC;

/**
 * Both a started (collect sensor data) and bounded service (update UI continuously)
 */
public class GameStateService extends Service implements SensorEventListener {
    private static final String TAG = "GameStateService";
    private static final int NOTIFY_ID = 0;
    private static final int PENDINGINTENT_ID = 1;

    // binder interface helper
    public class GameStateBinder extends Binder {
        public GameStateService getService() {
            return GameStateService.this;
        }
    }
    private final IBinder binder = new GameStateBinder();

    // sensors
    private SensorManager sensorManager;
    private Sensor stepDetector;

    // timer
    Timer timer = new Timer();

    // notifications
    NotificationManager notificationManager;

    public GameStateService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Sensors
         * 1. Setting up sensors needed for the app.
         */
        // get handle to sensor device
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // get list of all available sensors, along with some capability data
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        String sensorsStr = "available sensors:";
        for (Sensor sensor: sensors) {
            sensorsStr += "\n" + sensor.getName() +
                    " madeBy=" + sensor.getVendor() +
                    " v" + sensor.getVersion() +
                    " minDelay=" + sensor.getMinDelay() +
                    " maxRange=" + sensor.getMaximumRange() +
                    " power=" + sensor.getPower();
        }
        Log.i(TAG, sensorsStr);

        // get handles to required sensors
        // - if you want to show app only if user has the sensor, then do <uses-feature> in manifest
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetector == null) {
            Log.e(TAG, "No step sensors on device!");
            System.exit(1);
        }

        // init notification manager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * Sensors.
         * 3. Registering listener to listen for sensor events.
         */
        // note that the DELAY is the max, and system normally lower
        // - don't just use SENSOR_DELAY_FASTEST (0us) as it uses max power
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_GAME);

        // init timer in thread
        Thread t = new Thread()  {
            @Override
            public void run() {
                super.run();
                try {
                    while (true) {
                        // decrement countdown every sec
                        sleep(1000);
                        GameState.i().decTimer();

                        // notify user when bug is spawning
                        if (GameState.i().isCanNotify() && !GameState.i().isAppActive()) {
                            Log.i(TAG, "The NIGHT has come: a bug will spawn...");

                            // create pending intent to open app
                            Intent intent = new Intent(GameStateService.this, AndroidLauncher.class);
                            PendingIntent pi = PendingIntent.getActivity(GameStateService.this, PENDINGINTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            // build the notification
                            Notification.Builder builder = new Notification.Builder(GameStateService.this)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle("Exercise Or Die")
                                    .setColor(Color.RED)
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .setVisibility(VISIBILITY_PUBLIC)
                                    .setContentText("OMG NIGHT TIME lai liao, BUGs will spawn")
                                    .setAutoCancel(true)
                                    //.setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                                    .setLights(Color.RED, 3000, 3000)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setContentIntent(pi);
                            notificationManager.notify(NOTIFY_ID, builder.build());
                        }
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        t.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this, stepDetector);
    }

    /**
     * Sensors
     * 2. Implementing SensorEventListener functions.
     */
    // callback when sensor has new values
    // - do as minimal as possible (this is called VERY frequently)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            int val = (int) event.values[0];

            // update game state based on sensor vals
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                Log.d(TAG, "Step detector:" + val);
                GameState.i().incSteps(val);
            }
        }
    }

    // callback when accuracy changes
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "Sensor accuracy changed to " + accuracy);
    }
}
