/**
 * WEEK09 LECTURE:
 * 1. revise asynctask
 * 2. revise viewmodel
 * 3. handlerthread
 * 4. static broadcast receiver
 * 5. dynamic broadcast receiver
 */
package com.boliao.eod

import android.app.Activity
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.boliao.eod.AndroidLauncher
import com.boliao.eod.Splash
import java.lang.ref.WeakReference

/**
 * This is the splash view that records who is playing.
 */
class Splash : AppCompatActivity() {
    var pref: SharedPreferences? = null
    var prefEditor: SharedPreferences.Editor? = null
    private var startAndroidLauncher: Intent? = null

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
        prefEditor = pref!!.edit() //TODO refactor to safe call

        // show splash text
        msgTxtView.setText(R.string.welcome_note)

        // TODO NETWORKING 1: init volley network request queue "singleton"
        NetworkRequestQueue.init(this)

        // TODO THREADING 2: create a persistent weather widget
        // - WeatherRepo is already nicely linked up in MVVM with SplashViewModel
        // - implement background weather fetching in WeatherRepo
        // Q: Do I (Splash Activity) need to know about WeatherRepo?
        val splashViewModel: ViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        (splashViewModel as SplashViewModel).weatherData.observe(this, Observer { s -> weatherTxtView.text = s })

        // start game on click "PLAY"
        playBtn.setOnClickListener {
            // TODO SERVICES 1: check if username is already taken
            // - if username exists, set msgTxtView to "player exists..."
            // - else, set msgTxtView to "starting game, pls wait"
            val username = usernameEdtTxt.text.toString()
            if (pref!!.contains(username)) {
                msgTxtView.text = "Name already exists!"
            } else {
                // Store  username to survive app destruction
                // DEPRECATED due to encryption below
                /*
                    msgTxtView.setText("Starting game...");
                    prefEditor.putString(username, username);
                    prefEditor.commit();
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
                // - I know know this encryption the most takes 5secs
                // - user needs to know result of what happened to his name anyway
                // SOLN: use AsyncTask
                EncryptTask(this@Splash).execute(username)

                // launch the game
            }

            // TODO SERVICES n: goto AndroidLauncher
        }
    }

    /**
     * AsyncTask to "encrypt" username
     * - heavy lifting in the background to be posted back to UI
     * - static class so as to prevent leaks
     * - need a ref to update UI thread, so use WeakReference (a.k.a. shared_ptr)
     * - onProgressUpdate(Integer... progress) left as an exercise
     * - note: publishProgress(Integer) is in built to pass progress to above from doInBackground
     */
    private class EncryptTask(act: Activity) : AsyncTask<String?, Void?, Boolean>() {
        // this is to get all the UI elements
        // - use weak reference so that it does not leak mem when activity gets killed
        var wr_act: WeakReference<Activity>

        override fun onPreExecute() {
            super.onPreExecute()
            val act = wr_act.get()
            if (act != null) {
                (act.findViewById<View>(R.id.msg_txtview) as TextView).text = "encrypting"
            }
        }

        override fun doInBackground(vararg str: String?): Boolean {
            try {
                Thread.sleep(3000)
                // do something to the str
            } catch (e: InterruptedException) {
                return false
            }
            return true
        }

        override fun onPostExecute(b: Boolean) {
            super.onPostExecute(b)
            val act = wr_act.get()
            if (act != null) {
                (act.findViewById<View>(R.id.msg_txtview) as TextView).text = "The encryption is:$b"
                (act as Splash).launchGame()
            }
        }

        init {
            wr_act = WeakReference(act)
        }
    }

    companion object {
        private const val TAG = "Splash"
        // shared preferences setup
        const val PREF_FILENAME = "com.boliao.eod.prefs"
    }
}