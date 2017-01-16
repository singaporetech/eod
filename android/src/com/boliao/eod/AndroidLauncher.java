package com.boliao.eod;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements SensorEventListener {
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

        // get sensor manager and sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(game, config);
	}

    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            int val = (int) event.values[0];

            // print sensor vals
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                System.out.println("Step counter:" + val);
            }
            else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                gameState.steps += val;
                //mTextView.setText(gameState.steps + " steps taken");
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, stepCounter);
        sensorManager.unregisterListener(this, stepDetector);
    }

    public void onPause() {
        super.onPause();
    }
}
