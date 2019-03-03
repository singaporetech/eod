package com.boliao.eod;

//import androidx.annotation.NonNull;
//import androidx.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.arch.lifecycle.MutableLiveData;

public class WeatherRepo {
    // Test var
    private static int count = 0;

    // interval between fetching data
    private static final int FETCH_INTERVAL_MILLIS = 2000;

    // singleton
    private static WeatherRepo instance;

    private Runnable r;

    // weather live data
    @NonNull
    private MutableLiveData<String> weatherData = new MutableLiveData<>();

    // get singleton
    public static WeatherRepo getInstance() {
        if (instance == null) {
            synchronized (WeatherRepo.class) {
                if(instance == null)
                    instance = new WeatherRepo();
            }
        }
        return instance;
    }

    @NonNull
    public MutableLiveData<String> getWeatherData() {
        return weatherData;
    }

    public void mockOnlineWeatherData() {
        weatherData.postValue("Mock Weather Data");
    }

    /**
     * Background continuous task to fetch weather data
     * - Spawn a HandlerThread
     * - Q: Should you use a service to wrap the thread?
     * - A: Depends on whether you want it running beyond visible lifecycle
     */
    public void fetchOnlineWeatherData() {
       final WeatherWorkerThread weatherWorkerThread = new WeatherWorkerThread();
       weatherWorkerThread.start();
       weatherWorkerThread.prepareHandler();

       r = new Runnable() {
           @Override
           public void run() {
               weatherData.postValue("Weather now is: " + count++);
               weatherWorkerThread.postTaskDelayed(r, FETCH_INTERVAL_MILLIS);
           }
       };
       weatherWorkerThread.postTask(r);
    }
}
