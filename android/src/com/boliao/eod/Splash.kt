/**
 * # WEEK11: NDK
 * Communicating data across kotlin and C/C++ code.
 *
 * 1. Add NDK development capabilities to existing project
 * 2. Interfacing with a native C lib - ARCore
 */

package com.boliao.eod

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.coroutines.*

/**
 * This is the splash view that records who is playing.
 */
class Splash : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var startAndroidLauncher: Intent

    // TODO NDK 0: install required dependencies (from Android Studio SDK Tools) // - NDK: Android toolset to communicate with native code
    // - CMake: native build tool
    // - LLDB: native code debugger

    // TODO NDK 1: create the native code and build configuration
    // - create a src/cpp directory
    // - create a new .cpp file
    // - create a CMake build script called CMakeLists.txt (https://developer.android.com/studio/projects/configure-cmake.html)
    // - check that the .cpp path (relative to script) is correct in the CMake script
    // - make sure you add_library for your own libs and find_library for Android NDP native libs
    // - then link the libs together using target_link_libraries
    // - add CMake path in gradle (you can right click on folder and let IDE do it)
    // - may need to restart project afer configuration

    // TODO NDK 2: create a test method in native to receive a string and show it
    // - declare a native function you want to write in C/C++
    // - write the method in a cpp file
    // - load native lib and declare native methods
    // - paste a native function in the cpp file, and use IDE helper to fill in method name
    // - receive string from native function and display it in a toast
    // - try and debug within native using <android/log.h>

    // TODO NDK 3: use ARCore C lib to place things via the cam
    // - https://developers.google.com/ar/develop/c/quickstart
    // - requires https://github.com/google-ar/arcore-android-sdk/releases/tag/v1.15.0
    // - then install via
    //   adb install -r Google_Play_Services_for_AR_1.15.0_x86_for_emulator.apk
    // - clone the ARCore repo
    // - git clone https://github.com/google-ar/arcore-android-sdk.git
    // - run some samples

    private fun launchGame() {
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

        // TODO NETWORKING 1: init the network request queue singleton object (volley)
        // - goto NETWORKING 0 in manifest
        // - create NetWorkRequestQueue singleton
        // - set NetworkRequestQueue's context to this
        // - goto NETWORKING 2 in WeatherRepo
        NetworkRequestQueue.setContext(this)

        // val splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        // val splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        val splashViewModel: SplashViewModel by viewModels()
        splashViewModel.weatherData.observe(this) {
            weatherTxtView.text = it
        }

        splashViewModel.loginStatus.observe(this) {
            if (it) {
                msgTxtView.text = "LOGIN DONE. Starting..."
                launchGame()
            } else {
                msgTxtView.text = "Name OREDI exist liao..."
            }
        }

        // start game on click "PLAY"
        playBtn.setOnClickListener {
            msgTxtView.text = "Encrypting in coroutine heaven..."
            splashViewModel.login(usernameEdtTxt.text.toString())
        }

        // provide a way to stop the service
        findViewById<Button>(R.id.exit_btn).setOnClickListener {
            AndroidLauncher.startServiceIntent?.let {
                stopService(it)
            }
            finish()
        }

        // TODO NDK 2: show the string from native
        Toast.makeText(this, getNativeString(), Toast.LENGTH_LONG).show()
    }

    private external fun getNativeString(): String

    companion object {
        private const val TAG = "Splash"

        init {
            System.loadLibrary("core-lib")
        }

        /**
         * [DEPRECATED] AsyncTask to "encrypt" username
         * - heavy lifting in the background to be posted back to UI
         * - static class so as to prevent leaks
         * - internal ctor to only allow enclosing class to construct
         * - need a ref to update UI thread, so use WeakReference (a.k.a. shared_ptr)
         * - onProgressUpdate(Integer... progress) left as an exercise
         * - note: publishProgress(Integer) is in built to pass progress to above from doInBackground
         */
        /*
        private class EncryptTask internal constructor(act: Splash) : AsyncTask<String?, Void?, Boolean>() {
            // hold the Activity to get all the UI elements
            // - use weak reference so that it does not leak mem when activity gets killed
            var wr_splash: WeakReference<Splash> = WeakReference(act)

            override fun onPreExecute() {
                super.onPreExecute()
                val splash = wr_splash.get()
                if (splash != null) {
                    (splash.findViewById<View>(R.id.msg_txtview) as TextView).text = "encrypting"
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
                val splash = wr_splash.get()
                if (splash != null) {
                    (splash.findViewById<View>(R.id.msg_txtview) as TextView).text = "The encryption is:$b"
                    splash.launchGame()
                }
            }
        }
         */
    }
}