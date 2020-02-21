package com.boliao.eod

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {
    // weather live data (read-only)
    var weatherData: LiveData<String>

    init {
        // TODO THREADING: boot up the WeatherRepo singleton to start fetching weather data
        // - only I control the repo, my boss (Activity) does not need to know
        WeatherRepo.fetchMockOnlineWeatherData()

        // TODO NETWORKING 3: call WeatherRepo to fetch online weather instead

        // link up live data to repo (observer pattern)
        weatherData = WeatherRepo.weatherData
    }
}