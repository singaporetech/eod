package com.boliao.eod;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    private static final String TAG = "AndroidLauncher";
    private static final int REMINDER_JOB_ID = 0;
    private Intent intent;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO SERVICES 3: create a reminder for user to charge phone periodically
        // - e.g., test a time of 5 secs
        JobScheduler js = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo ji = new JobInfo.Builder(
                REMINDER_JOB_ID,
                new ComponentName(this, ReminderJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(5*1000)
                .build();
        js.schedule(ji);

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
            startService(intent);
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
