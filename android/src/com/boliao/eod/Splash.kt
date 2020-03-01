/**
 * # WEEK09: THREADING
 * A persistent weather widget.
 *
 * 1. See the use of raw java threads in the bug spawning code in GameStateService
 * 2. Create an Asynctask to encrypt usernames in the background
 * 3. Create a weather worker Handlerthread to fetch weather updates in the background
 * 4. Replace Asynctask with coroutine approach
 */

package com.boliao.eod

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

/**
 * This is the splash view that records who is playing.
 *
 * TODO THREADING 4.1: implement CoroutineScope delegated to MainScope()
 * This allows any coroutine builder (launch, async) block to be within this Activity scope
 */
class Splash : AppCompatActivity(), CoroutineScope by MainScope() {
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

        splashViewModel.loginStatus.observe(this, Observer {
            if (it) {
                msgTxtView.text = "LOGIN DONE. Starting..."
                launchGame()
            } else {
                msgTxtView.text = "Name OREDI exist liao..."
            }
        })

        // start game on click "PLAY"
        playBtn.setOnClickListener {
            msgTxtView.text = "Encrypting in coroutine heaven..."
            splashViewModel.login(usernameEdtTxt.text.toString())
        }
    }

    companion object {
        private const val TAG = "Splash"

        /**
         * TODO THREADING 1: create a persistent weather widget
         * AsyncTask to "encrypt" username. Typical usecase: perform heavy lifting in the background
         * meant to be posted back to UI.
         * - make a static class so as to prevent leaks
         * - use internal ctor to only allow enclosing class to construct
         * - use WeakReference<Splash> (a.k.a. shared_ptr) as need a ref to update UI thread
         * - note: onProgressUpdate(Integer... progress) left as an exercise
         * - note: publishProgress(Integer) is in-built to pass progress to above from doInBackground
         * - with MVVM, this should be in VM, but AsyncTask was built to directly update UI
         * - we could also observe more LiveData here and AsyncTask lives in VM and updates the LiveData
         */
        /*
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
        */
    }
}