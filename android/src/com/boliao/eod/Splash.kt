/**
 * # WHAT IS THIS?
 * Example android app EOD using a mix of libraries, e.g., libgdx for graphics.
 * 1. browse through the code structure
 * 2. highlight expected commenting conventions
 *
 * # WEEK06: Putting (some of) it all together
 * This week we will look at some common Android Architecture Components through a running
 * example of a simple login feature that we will try to implement.
 * 0. see the necessary artifacts in build.gradle
 * 1. create a login view in Splash Activity and manage the login in the activity
 * 2. create a ViewModel component to manage the login with a LiveData component (i.e., use MVVM)
 * 3. create a Room component to manage the login data more comprehensively
 *
 * # Extras:
 * 1. Managing Sprints through github
 *
 * # WEEK08: SERVICES
 * Run through several use cases for different background processing requirements.
 * 1. revise persistent storage options to determine if username exists.
 * 2. create a method in the view to generate a hard pw using some pseudo-cpu-intensive algo
 * 3. now use the VM to perform the pw generation task
 * 4. now use an IntentService for the pw generation task
 * 5. observe the started and bound service: GameStateService
 * 6. configure notifications for the GameStateService when bugs spawn
 * 7. convert the started service to a foreground service
 * 8. create a scheduled service (once app boots) to remind user to charge the phone periodically
 *
 * # WEEK09: THREADING (& NETWORKING)
 * Add a persistent weather widget.
 * 1. Observe the use of raw java threads in the bug spawning code in GameStateService
 * 2. Use an Asynctask to fetch (mock) weather updates in the background.
 * 3. Revamp the weather task using coroutines.
 * 3*.Also revamp the login task with coroutines if there is time.
 * 4. Now use a worker Handlerthread to do the above weather widget task in the background.
 * 5. Now use a coroutine to do the same weather widget
 * 6. Replace the mock weather task with real fetching from a RESTful API using the volley lib.
 */

package com.boliao.eod

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.boliao.eod.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import kotlin.random.Random

/**
 * Splash View to show the entry screen of the app.
 * - NOTE that some it may be more precise to refer to this as the Controller and the XML as the view
 * - MVCVM? :/
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
                (application as EODApp).playerRepo,
                (application as EODApp).weatherRepo)
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
        // NOTE I have more comments than necessary for demo purposes
        startAndroidLauncher = Intent(this@Splash, AndroidLauncher::class.java)

        // show splash text by default
        binding.msgTxtview.setText(R.string.welcome_note)

        // TODO THREADING 2.2: Use an Asynctask to fetch (mock) weather updates in the background.
        // 1. execute the AsyncTask here with some str data
//        WeatherTask(this).execute("NYP")

        // TODO THREADING 4: Use a Handlerthread in a Repo layer to fetch (mock) weather updates
        // 1. Create the Weather Handlerthread
        // 2. Use the Handlerthread in the WeatherRepo to fetch weather
        // 3. Expose a Livedata from the WeatherRepo through the SplashViewModel
        // 4. Observe the weather Livedata here on weatherTxtView
        splashViewModel.weatherData.observe(this, Observer {
            binding.weatherTxtview.text = it
        })

        // TODO NETWORKING 1: init the network request queue singleton object (volley)
        // - goto NETWORKING 0 in manifest
        // - create NetWorkRequestQueue singleton
        // - in EODApp, set NetworkRequestQueue's context to EODApp
        // - goto NETWORKING 2 in WeatherRepo

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
            val name = binding.nameEdtxt.text.toString()
            val age =
                    if (binding.ageEdtxt.text.toString() == "") 0
                    else binding.ageEdtxt.text.toString().toInt()

            // TODO SERVICES 2.1: brute force method of having the cpu-intensive pw generator here
            // 1. simulate naive approach to perform an 8-sec long pseudo-encrypt method here
            // 2. extend the Player, PlayerDAO, PlayerRepo to have access to a pw field
            // 3. modify the login method in VM to take in a pw as well to add to the db
            // 4. observe the UI when we run it this way...
//            val pw = getEncryptedPw(name)

            // TODO SERVICES 3.1: slightly better method by having the pw generator in the VM
            // 1. shift the pw generator method into the VM
            // 2. omit the pw arg into the login() in VM
//            val pw = null
//            splashViewModel.login(
////                    this,
//                    name, age
////                    , pw
//            )

            // TODO SERVICES 4.1: an even better pw generator using an IntentService
            // 1. create an IntentService for the encryption task
            // 2. include APP (not ACT) context in the login(args..) to start the service
            // QNS: so when do we use services?
            // Note that the WorkManager is the preferred way to do this now
//            splashViewModel.login(applicationContext, name, age)

            // TODO THREADING 3*: call a coroutine in the VM to do the login
            splashViewModel.loginWithCoroutines(name, age)
        }

        // observe login status changes from the VM
        splashViewModel.loginStatus.observe(this) {
            if (it) {
                binding.msgTxtview.text = "logging in..."

                // NOTE that launchGame is launching a View so should be here
                launchGame()
            } else
                binding.msgTxtview.text = "Name OREDI exists lah..."
        }

        // provide a way to stop the service
        binding.exitBtn.setOnClickListener {
            stopService(AndroidLauncher.startServiceIntent)
            finish()
        }
    }

    /**
     * TODO THREADING 3: use a coroutine to perform the above weather update task
     * 1. add a coroutine scope to the activity using the MainScope delegate
     * 2. write a suspend function to perform the mock network fetch
     * 3. launch a coroutine block in onResume to run the non-blocking network task
     *
     * NOTE
     *   - .launch is fire and forget, .async is execute for a deferred result
     *   - the launch block below is non-blocking
     *   - the scope should probably be defined with + jobs so that we can prevent leaks
     *   - explicitly definitely a scope to call within
     *     CoroutineScope(Dispatchers.Main).launch {
     *   - or via a custom built scope which can be cancelled with parentJob.cancel()
     *     splashScope.launch {
     */
//    override fun onResume() {
//        super.onResume()
//
//        launch { // this is fire-and-forget
////            delay(3000)
//            Log.i(TAG, "1")
//            binding.weatherTxtview.text = "coroutine start..."
//            // within the coroutine, a state machine manages the suspend/resume
//            binding.weatherTxtview.text = fetchMockWeather() // this is suspended to allow subsequent code to run
//            Log.i(TAG, "2")
//        }
//
//        launch {
//            Log.i(TAG, "3")
//            binding.weatherTxtview.text = "after coroutine start..." // this will not be blocked by the above
////        Thread.sleep(3000)
//            delay(3000) // delay is a suspend function so will let things come in to run
//            binding.weatherTxtview.text = "after coroutine end..."
//            Log.i(TAG, "4")
//        }
//    }

    /**
     * Suspend function to perform mock network task.
     * NOTE that for simplicity sake this logic is here but this is of course to be done at lower
     *      layers in the architecture.
     * NOTE also the qualified return syntax so that it returns the value at the withContext scope
     */
    private suspend fun fetchMockWeather(): String = withContext(Dispatchers.Main) {
//        Thread.sleep(5000)
        delay(5000)
        return@withContext "Todays mockery is ${Random.nextInt()}"
    }

    /**
     * TODO SERVICES 2.2: write pseudo-encryption function.
     *
     * A function that simply sleeps for 5s to mock a cpu-intensive task
     * It also does some amazing manip to the name string
     * @param name of the record to generate pw for
     * @return (pseudo-)encrypted pw String
     */
//    private fun getEncryptedPw(name: String): String {
//        Thread.sleep(8000)
//        return name + "888888"
//    }

    companion object {
        private val TAG = Splash::class.simpleName

        // TODO ARCH 1.1: Manage login data in view
        // 1. create unique FILENAME const to reference dataset
        // 2. create a SharedPreferences var to manage data
        const val PREF_FILENAME = "com.boliao.eod.prefs"
        private lateinit var pref: SharedPreferences

        /**
         * TODO THREADING 2.1: observe the asynctask approach for fetching (mock) weather updates
         * [DEPRECATED] AsyncTask to "encrypt" username
         * - static class so as to prevent leaks
         * - internal ctor to only allow enclosing class to construct
         * - onProgressUpdate(Integer... progress) left as an exercise
         *   publishProgress(Integer) is in built to pass progress to above from doInBackground
         */
        private class WeatherTask internal constructor(act: Splash) : AsyncTask<String?, Void?, Boolean>() {
            // hold the Activity to get all the UI elements
            // - use weak reference (a.k.a. share_ptr) so that it does not leak mem when
            //   activity gets killed
            var wr_splash: WeakReference<Splash> = WeakReference(act)

            override fun onPreExecute() {
                super.onPreExecute()
                val splash = wr_splash.get()
                splash?.let {
                    it.binding.weatherTxtview.text ="fetching weather"
                }
            }

            /**
             * Heavy lifting in the background to be posted back to UI
             * @param strs is a list of the data type we indicate (another thing to trip the unwary)
             * @return Boolean to indicate whether weather fetching was successful
             */
            override fun doInBackground(vararg strs: String?): Boolean {
                try {
                    Thread.sleep(3000)
                    // do something to the str
                    strs?.let {
                        Log.i(TAG, "in background of AsyncTask processing weather for ${it[0]}")
                    }
                } catch (e: InterruptedException) {
                    return false
                }
                return true
            }

            /**
             * Stuff to be done of the main thread after done background processing.
             * @param b the value returned from the processing
             */
            override fun onPostExecute(b: Boolean) {
                super.onPostExecute(b)
                val splash = wr_splash.get()
                splash?.let {
                    it.binding.weatherTxtview.text ="Today's weather is sunny"
                }
            }
        }
    }
}
