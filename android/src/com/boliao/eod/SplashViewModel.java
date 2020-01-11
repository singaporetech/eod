package com.boliao.eod;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
//import android.support.annotation.NonNull;
//import android.arch.lifecycle.ViewModel;
//import android.arch.lifecycle.LiveData;

public class SplashViewModel extends ViewModel {
    // weather live data (read-only)
    @NonNull
    LiveData<String> weatherData;
    @NonNull
    public LiveData<String> getWeatherData() {
        return weatherData;
    }

    public SplashViewModel() {
        super();

        // TODO: boot up the WeatherRepo singleton to start fetching weather data
        // - only I control the repo, my boss (Activity) does not need to know
//        WeatherRepo.getInstance().mockOnlineWeatherData();
        WeatherRepo.getInstance().fetchOnlineWeatherData();

        // link up live data to repo
        weatherData = WeatherRepo.getInstance().getWeatherData();
    }

}
