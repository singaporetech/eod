package com.boliao.eod

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    // shared preferences setup
    private val PREF_FILENAME = "com.boliao.eod.prefs"
    private val pref: SharedPreferences

    // live weather data (read-only)
    // - this is bound to the mutable one in repo
    var weatherData: LiveData<String>

    // live login status
    val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    init {
        // setup shared preferences
        pref = getApplication<Application>().applicationContext
                .getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE)

        // TODO THREADING 4: replace the stub by the new threaded weather data method
        // - only I control the repo, my boss (Activity) does not need to know about repo
        // WeatherRepo.fetchStaticMockWeatherData()
        WeatherRepo.fetchDynamicMockWeatherData()

        // TODO NETWORKING 3: call WeatherRepo to fetch online weather instead

        // link up live data to repo (observer pattern)
        weatherData = WeatherRepo.weatherData
    }

    /**
     * Login using a username
     * Runs a coroutine in the VM in-built scope
     */
    fun login(username:String) = viewModelScope.launch {
        if (pref.contains(username))
            _loginStatus.postValue(false)
        else {
            // encrypt username
            val encryptedUsername = encrypt(username)

            // store in pref
            pref.edit().putString(username, encryptedUsername).apply()

            // update UI
            _loginStatus.postValue(true)
        }
    }

    /**
     * Coroutine for encryption.
     * Input username string and output encrypted username string.
     * Use Dispatchers.Default to place this work to the background Default thread in case the
     * caller of this coroutine is calling via Dispatchers.Main .
     *
     * <Kotlin official defn>
     * One can think of a coroutine as a light-weight thread. Like threads, coroutines can run in
     * parallel, wait for each other and communicate. The biggest difference is that coroutines are
     * very cheap, almost free: we can create thousands of them, and pay very little in terms of
     * performance. True threads, on the other hand, are expensive to start and keep around.
     * A thousand threads can be a serious challenge for a modern machine.
     */
    private suspend fun encrypt(username: String) = withContext(Dispatchers.Default) {
        // THE encryption :)
        delay(10000)
        return@withContext username
    }

    override fun onCleared() {
        super.onCleared()

        // remember to play safe, no leaks
        viewModelScope.cancel()
    }
}