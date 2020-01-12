package com.boliao.eod

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

//import android.support.annotation.NonNull;
//import android.arch.lifecycle.ViewModel;
//import android.arch.lifecycle.LiveData;
class SplashViewModel : ViewModel() {
    // weather live data (read-only)
    var weatherData: LiveData<String>

    init {
        // TODO: boot up the WeatherRepo singleton to start fetching weather data
        // - only I control the repo, my boss (Activity) does not need to know
        //        WeatherRepo.getInstance().mockOnlineWeatherData();
        WeatherRepo.fetchOnlineWeatherData()

        // link up live data to repo
        weatherData = WeatherRepo.weatherData
    }
}