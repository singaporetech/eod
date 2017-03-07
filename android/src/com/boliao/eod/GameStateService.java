package com.boliao.eod;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.badlogic.gdx.utils.Timer;

import java.util.List;

/**
 * Both a started (collect sensor data) and bounded service (update UI continuously)
 */
public class GameStateService extends Service implements SensorEventListener {
    private static final String TAG = "GameStateService";

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
    }

    /**
     * Sensors.
     * 3. Registering listener to listen for sensor events.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // note that the DELAY is the max, and system normally lower
        // - don't just use SENSOR_DELAY_FASTEST (0us) as it uses max power
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_GAME);

        // init timer in thread that decreases every sec
        Thread t = new Thread()  {
            @Override
            public void run() {
                super.run();
                try {
                    while (true) {
                        sleep(1000);
                        GameState.i().decTimer();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        t.start();

//        timer.scheduleTask(new Timer.Task() {
//            @Override
//            public void run() {
//                GameState.i().decTimer();
//            }
//        }, 1, 1);

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
