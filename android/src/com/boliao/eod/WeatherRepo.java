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

    private Runnable weatherRunner;

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
     * TODO THREADING 2: override this method to fetch weather data
     * - Background continuous task to fetch weather data
     * - always updating regularly (confirm < 15min) from online API
     * - not expecting to pause it at any point until user logs in, where we'll stop it manually
     * - ideally want updates even if navigate away (or even destroyed)
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
