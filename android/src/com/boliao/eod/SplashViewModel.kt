package com.boliao.eod

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {
    // weather live data (read-only)
    var weatherData: LiveData<String>

    init {
        // TODO THREADING 4: replace the stub by the new threaded weather data method
        // - only I control the repo, my boss (Activity) does not need to know about repo
        // WeatherRepo.fetchStaticMockWeatherData()
        WeatherRepo.fetchTimedMockWeatherData()

        // TODO NETWORKING 3: call WeatherRepo to fetch online weather instead

        // link up live data to repo (observer pattern)
        weatherData = WeatherRepo.weatherData
    }
}