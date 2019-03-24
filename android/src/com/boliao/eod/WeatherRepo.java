package com.boliao.eod;

//import androidx.annotation.NonNull;
//import androidx.lifecycle.MutableLiveData;
import android.app.Application;
import android.support.annotation.NonNull;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Where most of the hard work gets done... at the lower levels...
 */
public class WeatherRepo {
    private static final String TAG = "WeatherRepo";

    // Mocking var
    private static int count = 0;

    // interval between fetching data
    private static final int FETCH_INTERVAL_MILLIS = 2000;

    // threading
    private Runnable weatherRunner;

    // weather live data (writable)
    @NonNull
    private MutableLiveData<String> weatherData = new MutableLiveData<>();
    @NonNull
    MutableLiveData<String> getWeatherData() {
        return weatherData;
    }

    // singleton pattern boilerplate
    private static WeatherRepo instance;
    static WeatherRepo getInstance() {
        if (instance == null) {
            synchronized (WeatherRepo.class) {
                if(instance == null)
                    instance = new WeatherRepo();
            }
        }
        return instance;
    }

    /**
     * Mock live data.
     */
    void mockOnlineWeatherData() {
        weatherData.postValue("Mock Weather Data");
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
    void fetchMockOnlineWeatherData() {
        final WeatherWorkerThread weatherWorkerThread = new WeatherWorkerThread();
        weatherWorkerThread.start();
        weatherWorkerThread.prepareHandler();

        weatherRunner = new Runnable() {
            @Override
            public void run() {
                weatherData.postValue("Weather now is: " + count++);
                weatherWorkerThread.postTaskDelayed(weatherRunner, FETCH_INTERVAL_MILLIS);
            }
        };
        weatherWorkerThread.postTask(weatherRunner);
    }

    /**
     * TODO NETWORKING 1: fetch real online weather data from RESTful API
     * - volley is fast and clean
     * - many APIs were created pre-MVVM, so needed context to init, in this case the request queue
     * - note how I've used the semi singleton to resolve the context issue
     * - others have used empty Application classes
     */
    void fetchOnlineWeatherData() {
        final String urlStr = "https://api.data.gov.sg/v1/environment/2-hour-weather-forecast?date=" +
                getToday();
        Log.i(TAG, "Fetching online weather data: url=" + urlStr);

        // form the network request complete with response listener
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlStr, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "volley fetched \n" + response.toString());
                try {
                    // parse the returned json
                    String forecastStr = response.getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONArray("forecasts")
                            .getJSONObject(0)
                            .getString("forecast");

                    // post to live data here
                    weatherData.postValue("Weather now is " + forecastStr);
                } catch (JSONException e) {
                    Log.e(TAG, "json exception: " + e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error while fetching :" + error.getLocalizedMessage());
            }
        });

        NetworkRequestQueue.getInstance().add(request);
    }

    /**
     * Helper to get today's date in API format for network request.
     * @return
     */
    String getToday() {
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.format(time);
    }
}
