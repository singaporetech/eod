package com.boliao.eod;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class AndroidLauncher extends AndroidApplication {
    private static final String TAG = "AndroidLauncher";
    private static final int REMINDER_JOB_ID = 0;
    private Intent intent;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO SERVICES 3: create a reminder for user to charge phone periodically
        // - note that periodic tasks cannot be < 15mins

        // build a set of constraints, e.g., battery low and device idle
        Constraints workConstraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build();

        // build a work request from a Worker.class that fires periodically with the constraints above
        PeriodicWorkRequest pwr = new PeriodicWorkRequest.Builder(ReminderWorker.class,
                15, TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        // enqueue the work request with the WorkManager singleton
        WorkManager.getInstance().enqueue(pwr);

        // TODO SERVICES 4: manage game state changes
        // - track and update steps
        // - track countdown timer to spawn bugs
        // - everything should be done in the background (even when app not visible)

        // start game state service
        // - may already be running from a previous run, so pls check
        intent = new Intent(this, GameStateService.class);
        if (GameState.i().isServiceStarted()) {
            Log.i(TAG, "GameStateService already started.");
        }
        else {
            Log.i(TAG, "Starting GameStateService...");
            GameState.i().setServiceStarted(true);
//            startService(intent);
            startForegroundService(intent); // TODO SERVICE 13:
         }

        // init game
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(Game.i(), config);
	}

    /**
     * Destroy all visuals but game state remains.
     * TODO store all positions and retrieve when restart
     */
    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroying activity only ");
        super.onDestroy();
        //stopService(intent);

        //Game.i().dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // set app to active
        GameState.i().setAppActive(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set app to active
        GameState.i().setAppActive(true);
    }
}
