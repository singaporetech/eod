package com.boliao.eod

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication() {
    private var startServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate!!!!!!!!l")

        // TODO SERVICES 4: manage game state changes
        // - track and update steps
        // - track countdown timer to spawn bugs
        // - everything should be done in the background (even when app not visible)
        // - start game state service
        // - may already be running from a previous run, so pls check
        startServiceIntent = Intent(this, GameStateService::class.java)
        Log.d(TAG, "config ${startServiceIntent.toString()}")
        if (GameState.i().isServiceStarted) {
            Log.i(TAG, "GameStateService already started.")
        } else {
            Log.i(TAG, "Starting GameStateService...")
            GameState.i().isServiceStarted = true
            startService(startServiceIntent)
        }
        // init game
        val config = AndroidApplicationConfiguration()
        Log.d(TAG, "config ${config.toString()}")
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

    companion object {
        private val TAG = AndroidLauncher::class.simpleName
    }
}