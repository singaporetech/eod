package com.boliao.eod

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication() {
    companion object {
        private val TAG = AndroidLauncher::class.simpleName
    }

    private lateinit var startServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate!")

        // TODO SERVICES 3: manage game state changes
        // - start game state service using an intent
        startServiceIntent = Intent(this, GameStateService::class.java)
        Log.d(TAG, "config $startServiceIntent")

        if (GameState.i().isServiceStarted) {
            Log.i(TAG, "GameStateService already started.")
        } else {
            Log.i(TAG, "Starting GameStateService...")
            GameState.i().isServiceStarted = true

            // TODO SERVICES 12: cont'd foreground service
            // - change to startForegroundService
            startService(startServiceIntent)
        }

        // init game
        val config = AndroidApplicationConfiguration()
        Log.d(TAG, "config $config")
        initialize(Game.i(), config)
    }

    /**
     * Destroy all visuals but game state remains.
     * TODO store all positions and retrieve when restart
     */
    override fun onDestroy() {
        Log.i(TAG, "Destroying activity only ")
        super.onDestroy()
        //stopService(intent);
        //Game.i().dispose();
    }

    override fun onPause() {
        super.onPause()

        // set app to active
        GameState.i().isAppActive = false
    }

    override fun onResume() {
        super.onResume()

        // set app to active
        GameState.i().isAppActive = true
    }
}