package com.boliao.eod

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

/**
 * The game activity launcher extended from AndroidApplication provided by libgdx.
 * - the core (game) module is in libgdx
 * - we can also add other platforms (e.g., iOS) in the platform module
 * - this is triggered by Splash
 */
class AndroidLauncher : AndroidApplication() {

    /**
     * Boot up the game state service and init the core Game module singleton.
     * @param savedInstanceState the usual bundle of joy
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate!")

        // TODO SERVICES 4: manage game state changes
        // - track and update steps
        // - track countdown timer to spawn bugs
        // - everything should be done in the background (even when app not visible)
        // - start game state service
        // - may already be running from a previous run, so pls check
        startServiceIntent = Intent(this, GameStateService::class.java)
        Log.d(TAG, "config $startServiceIntent")

        if (GameState.i().isServiceStarted) {
            Log.i(TAG, "GameStateService already started.")
        } else {
            Log.i(TAG, "Starting GameStateService...")
            GameState.i().isServiceStarted = true
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

    /**
     * Pause the game through the game state.
     */
    override fun onPause() {
        super.onPause()

        // set app to active
        GameState.i().isAppActive = false
    }

    /**
     * Resume the game through the game state.
     */
    override fun onResume() {
        super.onResume()

        // set app to active
        GameState.i().isAppActive = true
    }

    companion object {
        private val TAG = AndroidLauncher::class.simpleName
        lateinit var startServiceIntent: Intent
    }
}