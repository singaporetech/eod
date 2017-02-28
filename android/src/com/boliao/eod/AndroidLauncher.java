package com.boliao.eod;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.List;

public class AndroidLauncher extends AndroidApplication implements SensorEventListener {
    private static final String TAG = "AndroidLauncher";

    // init gameState singleton
    GameState gameState = GameState.i();

    // game singleton
    com.boliao.eod.Game game = com.boliao.eod.Game.i();

    // sensors
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private Sensor stepDetector;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Sensors
         * 1. Setting up sensors needed for the app.
         */
        // get handle to sensor device
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // get list of all available sensors, along with some capability data
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_STEP_DETECTOR);
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
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepCounter == null) {
            Log.e(TAG, "No step counter sensor on device!");
            System.exit(1);
        }
        if (stepDetector == null) {
            Log.e(TAG, "No step detector sensor on device!");
            System.exit(1);
        }

        // init game
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(game, config);
	}


    /**
     * Sensors
     * 2. Implementing listener functions.
     */
    // callback when sensor has new values
    // - do as minimal as possible (this is called VERY frequently)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            int val = (int) event.values[0];

            // print sensor vals
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                Log.d(TAG, "Step counter:" + val);
            }
            else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                gameState.incSteps(val);
            }
        }
    }

    // callback when accuracy changes
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "sensor accuracy changed to " + accuracy);
    }

    /**
     * Sensors.
     * 3. Registering listener to listen for sensor events.
     */
    @Override
    public void onResume() {
        super.onResume();

        // note that the DELAY is the max, and system normally lower
        // - don't just use SENSOR_DELAY_FASTEST (0us) as it uses max power
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_GAME);
    }

    // sensors are only "switched off" when app stops
    // - to save battery sensors should be off onPause (i.e., screen off)
    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, stepCounter);
        sensorManager.unregisterListener(this, stepDetector);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
