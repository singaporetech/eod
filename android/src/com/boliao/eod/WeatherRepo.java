package com.boliao.eod;

//import androidx.annotation.NonNull;
//import androidx.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.arch.lifecycle.MutableLiveData;

/**
 * Where most of the hard work gets done... at the lower levels...
 */
public class WeatherRepo {
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
    public MutableLiveData<String> getWeatherData() {
        return weatherData;
    }

    // singleton pattern boilerplate
    private static WeatherRepo instance;
    public static WeatherRepo getInstance() {
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
    public void mockOnlineWeatherData() {
        weatherData.postValue("Mock Weather Data");
    }

    /**
     * TODO THREADING 2: override method to fetch weather data
     * - background continuous task to fetch weather data
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
    public void fetchOnlineWeatherData() {
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
}
