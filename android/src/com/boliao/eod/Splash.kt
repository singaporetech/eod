/**
 * NOTE that ARCH lecture has extended video which completes the Room exercise.
 *      Remember to perform git branching and PR processes.
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
//        WeatherTask(this).execute("NYP")

        // TODO THREADING 4: Use a Handlerthread in a Repo layer to fetch (mock) weather updates
        // 1. Look at the arch layers EODApp->Splash->SplashViewModel->WeatherRepo
        // 1. Create the Weather Handlerthread
        // 2. Use the Handlerthread in the WeatherRepo to fetch weather
        // 3. Expose a Livedata from the WeatherRepo through the SplashViewModel, to be observed here
        splashViewModel.weatherData.observe(this, Observer {
            binding.weatherTxtview.text = it
        })

        // TODO NETWORKING 1: init the network request queue singleton object (volley)
        // - goto NETWORKING 0 in manifest
        // - create NetWorkRequestQueue singleton
        // - in EODApp, set NetworkRequestQueue's context to EODApp
        // - goto NETWORKING 2 in WeatherRepo

        // PLAY button actions
        binding.playBtn.setOnClickListener {
            val name = binding.nameEdtxt.text.toString()
            val age =
                    if (binding.ageEdtxt.text.toString() == "") 0
                    else binding.ageEdtxt.text.toString().toInt()

            // A pw generator using an IntentService
//            splashViewModel.login(applicationContext, name, age)

            // TODO THREADING 3*: call a coroutine in the VM to do the login
            splashViewModel.loginWithCoroutines(name, age)
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
     *
     * NOTE
     *   - launch and async are coroutine builders and are not suspend functions themselves
     *   - withContext is a suspend function
     *   - .launch is fire and forget, .async is execute for a deferred result
     *   - suspend and thread-blocking are different
     *   - the launch block below is non-blocking (non suspend) and will just register everything
     *     inside it and let the subsequent code run. But what is inside are suspend funcs and
     *     will all be suspended
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
     * TODO THREADING 3: use a coroutine to perform the above weather update task
     * (we ignore our nice arch layers first to illustrate how coroutines function)
     * 1. add a coroutine scope to the activity using the MainScope delegate
     * 2. write a suspend function to perform the mock network fetch
     * 3. launch a coroutine block in onResume to run the non-blocking network task
     * QNS: What if we can construct the weather str from 2 independent jobs?
     *
     * NOTE
     *   - launch and async are coroutine builders and are not suspend functions themselves
     *   - withContext is a suspend function
     *   - .launch is fire and forget, .async is execute for a deferred result
     *   - suspend and thread-blocking are different
     *   - the launch block below is non-blocking and will just register everything
     *     inside it and let the subsequent code run. But what is inside are suspend funcs and
     *     will all be suspended until possible to start (other ops in the thread that are not
     *     suspend funcs)
     *   - the scope should probably be defined with + jobs so that we can prevent leaks
     *   - explicitly definitely a scope to call within
     *     CoroutineScope(Dispatchers.Main).launch {
     *   - or via a custom built scope which can be cancelled with parentJob.cancel()
     *     splashScope.launch {
     */
    override fun onResume() {
        super.onResume()

        var result1 = "empty1"
        var result2 = "empty2"
        val startTime = System.currentTimeMillis()

        // TODO 1. The task we need to do
//        Log.i(TAG, "1")
//        launch {
//            result1 = fetchMockWeather1()
//            binding.weatherTxtview.text = "Weather is ${result1}"
//        }
//        // NOTE when running in Dispatchers.Main,
//        //      these log statements are not run on Main thread so will be printed before display
//        //      BUT you should notice that the screen freezes until the work is done
//        Log.i(TAG, "2")
//        Log.i(TAG, "Time taken is ${(System.currentTimeMillis()-startTime)}")

        // TODO 2. NAIVE way to concat 2 parts from 2 independent funcs
//        Log.i(TAG, "1")
//        launch {
//            result1 = fetchMockWeather1()
//            result2 = fetchMockWeather2()
//            binding.weatherTxtview.text = "Weather is ${result1}${result2}"
//            Log.i(TAG, "2 with time taken ${(System.currentTimeMillis()-startTime)}")
//        }
//        Log.i(TAG, "3 with time taken ${(System.currentTimeMillis()-startTime)}")

        // TODO 3. Another NAIVE try... to show launch is like threads
        //      (trying changing both the Dispatchers to IO and one to main etc...)
//        Log.i(TAG, "1")
//        launch {
//            result1 = fetchMockWeather1()
//            Log.i(TAG, "2 with time taken ${(System.currentTimeMillis()-startTime)}")
//        }
//        launch {
//            result2 = fetchMockWeather2()
//            Log.i(TAG, "3 with time taken ${(System.currentTimeMillis()-startTime)}")
//        }
//        binding.weatherTxtview.text = "Weather is ${result1}${result2}"
//        Log.i(TAG, "4 with time taken ${(System.currentTimeMillis()-startTime)}")

        // TODO 4. RIGHT way to do independent work
        //         NOTE that now the results need to be Deferred so re-declare
        Log.i(TAG, "1")
        launch {
            val result1 = async {fetchMockWeather1()}
            val result2 = async {fetchMockWeather2()}
            binding.weatherTxtview.text = "Weather is ${result1.await()}${result2.await()}"
            Log.i(TAG, "2 with time taken ${(System.currentTimeMillis()-startTime)}")
        }
        Log.i(TAG, "3 with time taken ${(System.currentTimeMillis()-startTime)}")

        // USING ASYNC in another way
        // async is not a suspend function but a builder to start a coroutine
//        Log.i(TAG, "1")
//        val result1 = async { fetchMockWeather1() }
//
//        Log.i(TAG, "2")
//        val result2 = async { fetchMockWeather1() }
//
//        // .await is a suspend function and so needs to be in coroutine
//        Log.i(TAG, "3")
//        launch {
//            binding.weatherTxtview.text = "${result1.await()}\n${result2.await()}"
//            Log.i(TAG, "Time taken is ${(System.currentTimeMillis()-startTime)}")
//        }
//
//        Log.i(TAG, "4")
//        Log.i(TAG, "Time taken is ${(System.currentTimeMillis()-startTime)}")
//        //do other things

        // using launch in another way
//        Log.i(TAG, "1")
//        launch { result1 = fetchMockWeather1() }
//
//        Log.i(TAG, "2")
//        launch { result2 = fetchMockWeather2() }
//
//        // .await is a suspend function and so needs to be in coroutine
//        Log.i(TAG, "3")
//        binding.weatherTxtview.text = "weather is ${result1} ${result2}"
//        Log.i(TAG, "Time taken is ${(System.currentTimeMillis()-startTime)}")
//
//        Log.i(TAG, "4")
//        Log.i(TAG, "Time taken is ${(System.currentTimeMillis()-startTime)}")
//        //do other things

    }

    /**
     * Suspend function to perform mock network task.
     * NOTE using Thread.sleep() to show freezing UI, else using delay it is a suspend function that
     *      allows UI ops to sneak in appropriately to not freeze the screen
     * NOTE that for simplicity sake this logic is here but this is of course to be done at lower
     *      layers in the architecture.
     * NOTE also the qualified return syntax so that it returns the value at the withContext scope
     */
    private suspend fun fetchMockWeather1(): String = withContext(Dispatchers.IO){
        Log.i(TAG, "fetchMockWeather1 start in thread ${Thread.currentThread().name}")
        Thread.sleep(3000)
        Log.i(TAG, "fetchMockWeather1 end")
        return@withContext "HOT"
    }
    private suspend fun fetchMockWeather2(): String = withContext(Dispatchers.IO){
        Log.i(TAG, "fetchMockWeather2 start in thread ${Thread.currentThread().name}")
        Thread.sleep(5000)
        Log.i(TAG, "fetchMockWeather2 end")
        return@withContext " AND HUMID"
    }

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
