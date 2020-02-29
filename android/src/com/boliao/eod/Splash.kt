/**
 * # WEEK09: THREADING
 * A persistent weather widget.
 *
 * 1. See the use of raw java threads in the bug spawning code in GameStateService
 * 2. Create an Asynctask to encrypt usernames in the background
 * 3. Create a weather worker Handlerthread to fetch weather updates in the background
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
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.lang.ref.WeakReference

/**
 * This is the splash view that records who is playing.
 */
class Splash : AppCompatActivity() {
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

        // TODO THREADING 2: create a persistent weather widget
        // An MVVM Splash ViewModel is already set up.
        // Splash Activity View -> Splash ViewModel -> WeatherRepo Model
        // WeatherRepo currently has a mock stub to return static mock data, provided live by
        // weatherData in SplashViewModel.
        // - set up weatherTextView here to observe the weatherData
        // - goto WeatherRepo for THREADING 3
        // Q: Do I (Splash Activity) need to know about WeatherRepo?
        val splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        splashViewModel.weatherData.observe(
                this,
                Observer {
                    weatherTxtView.text = it
                }
        )

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
                // NameCryptionService.startActionFoo(this@Splash, username)

                // TODO THREADING 1: what if now, I want this result to be shown on UI
                // - I know know this encryption the most takes 5secs
                // - user needs to know result of what happened to his name anyway
                // SOLN: use AsyncTask
                // EncryptTask(this@Splash).execute(username)

                // TODO THREADING 2: DEPRECATE ASYNCTASKS, COROUTINES FTW
                // - convert encryption fun to suspend fun with delay
                // - in the Main thread CoroutineScope, launch a coroutine to call encrypt
                // - await encryption asynchronously and add username to pref after encryption
                // - can also use WithContext to use the Default threadpool to do heavy tasks
                // - .launch is fire and forget, .async is execute for a deferred result
                // - the launch block below is non-blocking
                CoroutineScope(Dispatchers.Main).launch {
                    // encrypt username
                    val encryptedUsername = encrypt(username)

                    // store in pref
                    pref.edit().putString(username, encryptedUsername).apply()

                    // launch the game
                    msgTxtView.text = "STARTING!"
                    launchGame()
                }

                // this will not be blocked by the above coroutine
                msgTxtView.text = "encrypting in coroutine heaven"
            }

            // TODO SERVICES n: goto AndroidLauncher
        }
    }

    /**
     * Coroutine for encryption.
     * Input username string and output encrypted username string.
     * Place the intensive work on the Default thread.
     *
     * <Kotlin official defn>
     * One can think of a coroutine as a light-weight thread. Like threads, coroutines can run in
     * parallel, wait for each other and communicate. The biggest difference is that coroutines are
     * very cheap, almost free: we can create thousands of them, and pay very little in terms of
     * performance. True threads, on the other hand, are expensive to start and keep around.
     * A thousand threads can be a serious challenge for a modern machine.
     */
    private suspend fun encrypt(username: String) =
        withContext(Dispatchers.Default) {
            // THE encryption :)
            delay(15000)
            return@withContext username
        }

    companion object {
        private const val TAG = "Splash"

        // shared preferences setup
        const val PREF_FILENAME = "com.boliao.eod.prefs"
        private lateinit var pref: SharedPreferences

        /**
         * TODO THREADING 1: create a persistent weather widget
         * AsyncTask to "encrypt" username. Typical usecase: perform heavy lifting in the background
         * meant to be posted back to UI.
         * - make a static class so as to prevent leaks
         * - use internal ctor to only allow enclosing class to construct
         * - use WeakReference<Splash> (a.k.a. shared_ptr) as need a ref to update UI thread
         * - note: onProgressUpdate(Integer... progress) left as an exercise
         * - note: publishProgress(Integer) is in-built to pass progress to above from doInBackground
         */
        private class EncryptTask internal constructor(act: Splash) : AsyncTask<String?, Void?, Boolean>() {
            // hold the Activity to get all the UI elements
            // - use weak reference so that it does not leak mem when activity gets killed
            var wr_splash: WeakReference<Splash> = WeakReference(act)

            override fun onPreExecute() {
                super.onPreExecute()
                val splash: Splash? = wr_splash.get()
                splash?.let {
                    it.findViewById<TextView>(R.id.msg_txtview).text = "encrypting"
                }
            }

            override fun doInBackground(vararg str: String?): Boolean {
                try {
                    Thread.sleep(3000)

                    // save username to prefs
                    pref.edit().putString(str[0], str[0]).apply()

                } catch (e: InterruptedException) {
                    return false
                }
                return true
            }

            override fun onPostExecute(b: Boolean) {
                super.onPostExecute(b)
                val splash: Splash? = wr_splash.get()
                splash?.let {
                    it.findViewById<TextView>(R.id.msg_txtview).text = "The encryption is:$b"
                    splash.launchGame()
                }
            }
        }
    }
}