/**
 * NOTE:
 *      - ARCH lecture has extended video which completes the Room exercise.
 *      - Remember to perform git branching and PR processes.
 *      - let's complete task 8 in WEEK08 first
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
 * 1. observe the use of raw java threads in the bug spawning code in GameStateService
 * 2. observe an Asynctask to fetch (mock) weather updates in the background
 * 3. revamp the weather task using coroutines
 * 4. observe the implementation of a real weather widget task, using:
 *    - a worker Handlerthread for jobs to land on in a background thread
 *    - a networking lib (Volley) to fetch data from a RESTful API
 * 5. now revamp the weather widget using coroutines
 * 6. also revamp the login task with coroutines if there is time
 */

package com.boliao.eod

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.boliao.eod.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

/**
 * Splash View to show the entry screen of the app.
 * - NOTE that some it may be more precise to refer to this as the Controller and the XML as the view
 * - MVCVM? :/
 * - shows some status info
 * - handles user login to enter the game
 */
class Splash : AppCompatActivity(){
    private lateinit var startAndroidLauncher: Intent
    private lateinit var binding:ActivitySplashBinding

    // TODO THREADING 4: see the addition of weatherRepo as a dependency for the VM
    // Get the VM with the dependencies injected through a Factory pattern
    private val splashViewModel: SplashViewModel by viewModels {
        val app = application as EODApp
        SplashViewModelFactory(app.playerRepo, app.weatherRepo)
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
        WeatherTask(this).execute("NYP")

        // TODO THREADING 4: Use a Handlerthread in a Repo layer to fetch (mock) weather updates
        // 1. Look at the arch layers EODApp->Splash->SplashViewModel->WeatherRepo
        // 2. Call observe on the weather Livedata

        // PLAY button actions
        binding.playBtn.setOnClickListener {
            val name = binding.nameEdtxt.text.toString()
            val age =
                    if (binding.ageEdtxt.text.toString() == "") 0
                    else binding.ageEdtxt.text.toString().toInt()

            // A pw generator using an IntentService
            splashViewModel.login(applicationContext, name, age)
        }

        // observe login status changes from the VM
        splashViewModel.loginStatus.observe(this, {
            if (it) {
                binding.msgTxtview.text = "logging in..."

                // NOTE that launchGame is launching a View so should be here
                launchGame()
            } else
                binding.msgTxtview.text = "Name OREDI exists lah..."
        })

        // provide a way to stop the service
        binding.exitBtn.setOnClickListener {
            stopService(AndroidLauncher.startServiceIntent)
            finish()
        }
    }

    /**
     * TODO THREADING 3: use a coroutine to perform the above weather update task
     * (we ignore our nice arch layers first to illustrate how coroutines function)
     * 1. add a coroutine scope to the activity using the MainScope delegate
     * 2. write a suspend function to perform the mock network fetch
     * 3. launch a coroutine block in onResume to run the non-blocking network task
     */


    companion object {
        private val TAG = Splash::class.simpleName

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
                    // mocking long running task
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
