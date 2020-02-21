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
    private const val FETCH_INTERVAL_MILLIS = 1000

    // threading
    private var weatherRunner: Runnable? = null

    // weather live data (writable)
    val weatherData = MutableLiveData<String>()

    /**
     * Mock live data.
     */
    fun fetchStaticMockWeatherData() {
        weatherData.postValue("Mock Weather Data")
    }

    /**
     * TODO THREADING 3: create method to fetch timed mock weather data
     * A background continuous task to fetch timed mock weather data
     * It should always updating regularly (confirm < 15min) from online API
     * Should not pause at any point
     * Ideally want updates even if navigate away
     * Q: What primitive should we use?
     *    Recurring WorkManager?
     *    IntentService?
     *    ThreadPoolExecutor?
     *    AsyncTask?
     * A: Spawn a HandlerThread
     *
     * Q: Should you use a service to wrap the thread?
     * A: Depends on whether you want it running beyond visible lifecycle
     *
     * - create a WeatherWorkerThread from HandlerThread class
     * - start the thread and prepare the handler (looper only available after init HandlerThread)
     * - create a Runnable to postValue to the weatherData (simply post a counter value)
     * - post the Runnable as a delayed task in the thread to time updates
     * - goto SplashViewModel for THREADING 4
     */
    fun fetchTimedMockWeatherData() {
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
     * Helper to get today's date in API format for network request.
     * @return
     */
    val today: String
        get() {
            val time = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            return dateFormat.format(time)
        }
}