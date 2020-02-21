/**
 * WHAT IS THIS?
 * Example android app using a mix of libraries with libgdx as the core.
 *
 * 0. browse through an overview of the code structure
 *
 * # WEEK08: SERVICES
 * Run through several use cases for different background processing requirements.
 *
 * 1. revise persistent storage by using prefs to determine if username exists.
 * 2. create an IntentService to "encrypt" username in background
 * 3. observe the started service: GameStateService
 * 4. observe the binding code provided in GameStateService
 * 5. configure notifications for the GameStateService when bugs spawn
 * 6. convert the started service to a foreground service
 * 7. create a scheduled service (once app boots) to remind user to charge the phone periodically
 */

package com.boliao.eod

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.lang.ref.WeakReference

/**
 * This is the splash view that records who is playing.
 */
class Splash : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var startAndroidLauncher: Intent

    fun launchGame() {
        startActivity(startAndroidLauncher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // init launch game intent
        startAndroidLauncher = Intent(this@Splash, AndroidLauncher::class.java)

        // get refs to UI components
        val playBtn = findViewById<Button>(R.id.play_btn)
        val usernameEdtTxt = findViewById<EditText>(R.id.name_edtxt)
        val msgTxtView = findViewById<TextView>(R.id.msg_txtview)
        val weatherTxtView = findViewById<TextView>(R.id.weather_txtview)

        // setup shared preferences
        pref = getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE)

        // show splash text
        msgTxtView.setText(R.string.welcome_note)

        val splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        // start game on click "PLAY"
        playBtn.setOnClickListener {
            // TODO SERVICES 1: check if username is already taken
            // - if username exists, set msgTxtView to "player exists..."
            // - else, set msgTxtView to "starting game, pls wait"
            val username = usernameEdtTxt.text.toString()
            if (pref.contains(username)) {
                msgTxtView.text = "Name already exists!"
            } else {
                // Store username to survive app destruction
                // DEPRECATED due to encryption below
                /*
                    msgTxtView.setText("Starting game...");
                    prefs.edit().putString(username, username);
                    prefs.edit().commit();
                    */

                // TODO SERVICES 2: what if this needs some intensive processing
                // - e.g., pseudo-encrypt the username using some funky algo
                // - store the encrypted username in shared prefs
                // - UI should not lag or ANR

                // SOLN: defer processing to an IntentService: do some heavy lifting w/o
                // UI then shutdown the service
                // - note that the WorkManager can also accomplish this
                NameCryptionService.startActionFoo(this@Splash, username)

                // TODO THREADING 1: what if now, I want this result to be shown on UI
                // I know know this encryption the most takes 5secs
                // and user needs to know result of what happened to his name anyway
                // SOLN: use AsyncTask
                // - build the AsyncTask in companion object section below
                // - call the AsyncTask(this).execute(username)

                // launch the game
                launchGame()
            }
        }
    }

    companion object {
        private const val TAG = "Splash"

        // shared preferences setup
        const val PREF_FILENAME = "com.boliao.eod.prefs"

        /**
         * TODO THREADING 1: cont'd... create a persistent weather widget using AsyncTask
         * AsyncTask to "encrypt" username. Typical usecase: perform heavy lifting in the background
         * meant to be posted back to UI.
         * - make a static class so as to prevent leaks
         * - use internal ctor to only allow enclosing class to construct
         * - use WeakReference<Splash> (a.k.a. shared_ptr) as need a ref to update UI thread
         * - note: onProgressUpdate(Integer... progress) left as an exercise
         * - note: publishProgress(Integer) is in-built to pass progress to above from doInBackground
         */
    }
}