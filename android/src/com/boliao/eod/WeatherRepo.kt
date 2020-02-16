package com.boliao.eod

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Where most of the hard work gets done... at the lower levels...
 * - note the kotlin super singleton syntax "object"
 */
object WeatherRepo {
    private val TAG = WeatherRepo::class.simpleName

    // Mocking var
    private var count = 0

    // interval between fetching data
    private const val FETCH_INTERVAL_MILLIS = 2000

    // threading
    private var weatherRunner: Runnable? = null

    // weather live data (writable)
    val weatherData = MutableLiveData<String>()

    /**
     * Mock live data.
     */
    fun mockOnlineWeatherData() {
        weatherData.postValue("Mock Weather Data")
    }

    /**
     * TODO THREADING 2: override method to fetch mock timed weather data
     * - background continuous task to fetch mock weather data
     * - always updating regularly (confirm < 15min) from online API
     * - not expecting to pause it at any point
     * - ideally want updates even if navigate away
     * - Q: what primitive should we use?
     * - Recurring WorkManager?
     * - IntentService?
     * - ThreadPoolExecutor?
     * - AsyncTask?
     * - A: Spawn a HandlerThread
     * - Q: Should you use a service to wrap the thread?
     * - A: Depends on whether you want it running beyond visible lifecycle
     */
    fun fetchMockOnlineWeatherData() {
        val weatherWorkerThread = WeatherWorkerThread()
        weatherWorkerThread.start()
        weatherWorkerThread.prepareHandler()
        weatherRunner = Runnable {
            weatherData.postValue("Weather now is: " + count++)
            weatherWorkerThread.postTaskDelayed(weatherRunner, FETCH_INTERVAL_MILLIS.toLong())
        }
        weatherWorkerThread.postTask(weatherRunner)
    }

    /**
     * TODO NETWORKING 1: fetch real online weather data from RESTful API
     * - volley is fast and clean
     * - many APIs were created pre-MVVM, so needed context to init, in this case the request queue
     * - note how I've used the semi singleton to resolve the context issue
     * - others have used empty Application classes
     */
    fun fetchOnlineWeatherData() {
        val urlStr = "https://api.data.gov.sg/v1/environment/2-hour-weather-forecast?date=" +
                today
        Log.i(TAG, "Fetching online weather data: url=$urlStr")

        // form the network request complete with response listener
        val request = JsonObjectRequest(Request.Method.GET, urlStr, null, Response.Listener { response ->
            Log.i(TAG, "volley fetched \n$response")
            try { // parse the returned json
                val forecastStr = response.getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONArray("forecasts")
                        .getJSONObject(0)
                        .getString("forecast")
                // post to live data here
                weatherData.postValue("Weather now is $forecastStr")
            } catch (e: JSONException) {
                Log.e(TAG, "json exception: " + e.localizedMessage)
            }
        }, Response.ErrorListener { error -> Log.e(TAG, "Volley error while fetching :" + error.localizedMessage) })
        NetworkRequestQueue.getInstance().add(request)
    }

    /**
     * Helper to get today's date in API format for network request.
     * @return
     */
    val today: String
        get() {
            val time = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            return dateFormat.format(time)
        }
}