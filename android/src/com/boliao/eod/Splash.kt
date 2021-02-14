/**
 * WHAT IS THIS?
 * Example android app using a mix of libraries, e.g., libgdx for graphics.
 * 1. browse through an overview of the code structure
 * 
 * # WEEK06: Putting (some of) it all together
 * This week we will look at some common Android Architecture Components through a running
 * example of a simple login feature that we will try to implement.
 * 0. see the necessary artifacts in build.gradle
 * 1. create a login view in Splash Activity and manage the login in the activity 
 * 2. create a ViewModel component to manage the login with a LiveData component (i.e., use MVVM)
 * 3. create a Room component to manage the login data more comprehensively
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
 *
 * # WEEK09: THREADING
 * A persistent weather widget.
 *
 * 1. See the use of raw java threads in the bug spawning code in GameStateService
 * 2. Create an Asynctask to encrypt usernames in the background
 * 3. Create a weather worker Handlerthread to fetch weather updates in the background
 * 4. Replace Asynctask with coroutine approach
 *
 * # WEEK10: RECEIVERS
 * A static receiver on boot for reminders and
 * dynamically broadcasting steps to be received by another app
 *
 * 1. adding a static OnBootReceiver ON_BOOT via the manifest
 * 2. create an intent to be dynamically broadcasted to the world (on your device)
 * # WEEK10.5: NETWORKING
 * Fetching and showing the weather from a RESTful API.
 *
 * 1. Setting network permissions
 * 2. Using networking libs Volley
 */

package com.boliao.eod

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.boliao.eod.databinding.ActivitySplashBinding
import kotlinx.coroutines.*

/**
 * Splash View to show the entry screen of the app.
 * - shows some status info
 * - handles user login to enter the game
 */
class Splash : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var startAndroidLauncher: Intent
    private lateinit var binding:ActivitySplashBinding

    // TODO ARCH 3: Manage membership data with a Room
    // 1. lazy init the Room DB
    // 2. lazy init the player repo with the DAO from the DB
    // This should be done at the application level in
    private val splashViewModel: SplashViewModel by viewModels {
        SplashViewModelFactory(
                (application as EODApp).repo
        )
    }

    /**
     * Helper function to start the game.
     * Android launcher will start the game state service.
     */
    fun launchGame() {
        startActivity(startAndroidLauncher)
    }

    /**
     * Setup all the UI elements and their connections with the VM.
     * @param savedInstanceState the usual bundle of joy
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init launch game intent
        startAndroidLauncher = Intent(this@Splash, AndroidLauncher::class.java)

        // show splash text by default
        binding.msgTxtview.setText(R.string.welcome_note)

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

        // Old ways of getting the view model...
        // val splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        // val splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        // TODO ARCH 1.2: Manage login data in view
        // 1. get shared prefs using the filename and set mode to private for this app
        // 2. set the play btn's onClickListener to handle login
        //    only allow login for unique users
        // 3. show status of login on screen
        // 4. do a rotation and see what happens
//        pref = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE)
//        binding.playBtn.setOnClickListener {
//            val username = binding.nameEdtxt.text.toString()
//            if (pref.contains(username)) {
//                binding.msgTxtview.text = "Have user liao lah..."
//            }
//            else {
//                pref.edit().putString(username, username).apply()
//                binding.msgTxtview.text = "logging in..."
//            }
//        }

        // TODO ARCH 2.1: Manage login data with ViewModel and LiveData (i.e., use MVVM)
        // 1. create a ViewModel (VM) component for this Splash View
        // 2. move the login data mgt to the VM
        // 3. reset the play btn's onClickListener to handle login through the VM
        // 4. create a LiveData component to hold the login status in the VM
        // 5. observe the login status in this View
        // start game on click "PLAY"

        // TODO ARCH 3: Manage membership data with a Room
        // 1. create an entity class to represent a single user record
        // 2. create a DAO to handle queries
        // 3. create a Room DB
        // 4. create a Repo to manage the database
        // 5. create an Application class to initialize repo and DAO (update the manifest app name)
        // 6. modify the VM to include the repo as input to the ctor
        // 7. manage the database through the VM
        binding.playBtn.setOnClickListener {
            splashViewModel.login(binding.nameEdtxt.text.toString())
        }
        splashViewModel.loginStatus.observe(this, {
            if (it) {
                binding.msgTxtview.text = "logging in..."

                // NOTE that launchGame is launching a View so should be here
                launchGame()
            }
            else
                binding.msgTxtview.text = "Name OREDI exists lah..."
        })

        // observe the weather data
        splashViewModel.weatherData.observe(this, Observer {
            binding.weatherTxtview.text = it
        })

        // provide a way to stop the service
        binding.exitBtn.setOnClickListener {
            stopService(AndroidLauncher.startServiceIntent)
            finish()
        }
    }

    companion object {
        private const val TAG = "Splash"

        // TODO ARCH 1.1: Manage login data in view
        // 1. create unique FILENAME const to reference dataset
        // 2. create a SharedPreferences var to manage data
        const val PREF_FILENAME = "com.boliao.eod.prefs"
        private lateinit var pref: SharedPreferences

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
