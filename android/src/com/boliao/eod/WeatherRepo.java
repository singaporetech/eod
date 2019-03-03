package com.boliao.eod;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class WeatherRepo {
    // singleton
    private static WeatherRepo instance;

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

    public void fetchOnlineWeatherData() {
        weatherData.postValue("TESTING");
    }
}
