package com.boliao.eod;

//import androidx.annotation.NonNull;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.LiveData;

public class SplashViewModel extends ViewModel {
    @NonNull
    LiveData<String> weatherData;

    public SplashViewModel() {
        super();

        // link up live data to repo
        weatherData = WeatherRepo.getInstance().getWeatherData();
    }

    @NonNull
    public LiveData<String> getWeatherData() {
        return weatherData;
    }
}
