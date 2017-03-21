package com.boliao.eod;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;


public class AndroidLauncher extends AndroidApplication {
    private static final String TAG = "AndroidLauncher";
    private Intent intent;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // init game state service
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
        Log.i(TAG, "Destroying activity and service");
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
