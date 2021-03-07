package com.boliao.eod

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.*
import org.json.JSONException
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*

/**
 * The Weather Repository that manages weather data.
 * @param networkRequestQueue is the Volley component that manages network requests.
 */
class WeatherRepo (private val networkRequestQueue: NetworkRequestQueue) {

    companion object {
        private val TAG = WeatherRepo::class.simpleName

        // interval between fetching data
        private const val FETCH_INTERVAL_MILLIS: Long = 1000
    }

    // mocking var
    private var count = 0

    // threading
    private lateinit var weatherRunner: Runnable

    // weather live data (writable)
    val weatherData = MutableLiveData<String>()

    // TODO THREADING: observe the setup of some basic networking arch
    // For the lib, we'll use volley as example here (key advantages: fast and clean)
    // 1. define some permissions in the manifest
    // 2. create NetWorkRequestQueue singleton
    // 3. in EODApp, init and link up the NetWorkRequestQueue with the App context
    // 4. in EODApp, init a weatherRepo with the NetworkRequestQueue
    // 5. form a Volley request with the URL
    //    - define a Response.Listener to handle the Response
    //    - define a handler to handle errors

    private val urlStr = "https://api.data.gov.sg/v1/environment/2-hour-weather-forecast?date_time=$today"
    private val request = JsonObjectRequest(
            Request.Method.GET,
            urlStr,
            null,
            { response ->
                // Log.i(TAG, "volley fetched \n$response")
                try { // parse the returned json
                    val areaStr = response.getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONArray("forecasts")
                            .getJSONObject(0)
                            .getString("area")
                    val forecastStr = response.getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONArray("forecasts")
                            .getJSONObject(0)
                            .getString("forecast")

                    // post to live data here
                    weatherData.postValue("Weather in $areaStr \nat\n${today.substring(11, 19)} \nis \n$forecastStr  ")

                } catch (e: JSONException) {
                    Log.e(TAG, "json exception: " + e.localizedMessage)
                }
            },
            { error ->
                Log.e(TAG, "Volley error while fetching :" + error.localizedMessage)
            }
    )

    /**
     * OPTIONAL TODO THREADING 3: override method to fetch timed weather data
     * - background continuous task to fetch weather data
     * - always updating regularly (confirm < 15min) from online API
     * - not expecting to pause it at any point
     * - ideally continue to updates as much as possible even if navigate away
     *
     * Q: what primitive should we use?
     * - Recurring WorkManager?
     * - IntentService?
     * - ThreadPoolExecutor?
     * - AsyncTask?
     * A: Spawn a HandlerThread
     * Q: Should you use a service to contain the thread?
     * A: Depends on whether you want it running beyond visible lifecycle
     *
     * - create a WeatherWorkerThread from HandlerThread class
     * - start the thread and prepare the handler (looper only available after init HandlerThread)
     * - create a Runnable to postValue to the weatherData (simply post a counter value)
     * - post the Runnable as a delayed task in the thread to time updates
     * - goto SplashViewModel for THREADING 4
     *
     *
     * - use the HandlerThread primitive to make timed requests to the web API
     * - goto SplashViewModel for NETWORKING 3
     * NOTE that posting to the Livedata is done in the volley callback
     */
    fun fetchOnlineWeatherData() {
        val weatherWorkerThread = WeatherWorkerThread()
        weatherWorkerThread.start()
        weatherWorkerThread.prepareHandler()
        weatherRunner = Runnable {
            // DEBUG mocking weather with count
            // weatherData.postValue("Weather now is: ${count++}")

            // add request (with it's async response) to the volley queue
            networkRequestQueue.add(request)
            weatherWorkerThread.postTaskDelayed(weatherRunner, FETCH_INTERVAL_MILLIS.toLong())
        }
        weatherWorkerThread.postTask(weatherRunner)
    }

    /**
     * TODO THREADING 5: use a coroutine to fetch the weather
     * 1. create a suspend fun that dispatches to the IO threadpool
     * 2. make an infinite loop adds a volley request to the queue regularly
     *
     * NOTE that Repo can't start any coroutines since it does not have a LifeCycle to manage it
     *      - but it can certainly provide the suspend function (which we are not using here)
     *      - if threading needs to be handled here, it is a perfect use case for low level handlerthreads
     */

    /**
     * Helper to get today's date in API format for network request.
     * @return String in the format that the data.gov.sg API likes
     */
    val today: String
        get() {
            val time = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            return dateFormat.format(time)
        }
}
