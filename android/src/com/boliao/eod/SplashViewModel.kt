package com.boliao.eod

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

/**
 * Splash VM to manage the data logic between splash view and the model
 */
class SplashViewModel(
        private val playerRepo: PlayerRepo,
        private val weatherRepo: WeatherRepo) // TODO THREADING 4: add WeatherRepo dependency
    : ViewModel() {

    // live login status
    // NOTE that LiveData is a type of lifecycle-aware component
    // - manage functions that react to LifecycleOwners - e.g., Activity/Fragments/Services
    // - rather than the gazillion-responsibility dictatorship Activity class handling components
    //   using the onStart(), onResume() etc, now the responsibility falls on the individuals,
    //   like empowering students to do student-directed learning
    // - we can manually add lifecycle-aware components with
    //   someLifecycleOwner.getLifeCycle().addObserver(SomeLifecycleObserver())
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    // live member records from the Room DB
    val allPlayers: LiveData<List<Player>> = playerRepo.allPlayers.asLiveData()

    // live weather data (read-only)
    // - this is bound to the mutable one in repo
    var weatherData: LiveData<String>

    init {
        // TODO THREADING 4: start the WeatherRepo engine running to fetch online weather
        viewModelScope.launch {
            weatherRepo.fetchOnlineWeatherData()
        }

        // link up live data to repo (observer pattern)
        weatherData = weatherRepo.weatherData
    }

    /**
     * SERVICES 2.3: login with pw generation using an intent service
     * 1. create the IntentService class
     * 2. defer the heavy lifting pw generation task
     * 3. let it store the encrypted pw into the db when it is done
     * @return (pseudo-)encrypted String
     */
    fun login(context: Context, username:String, age:Int?) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "in view model login ${playerRepo.contains(username)}")

        if(playerRepo.contains(username)) {
            _loginStatus.postValue(false)
        }
        else {
            playerRepo.insert(Player(username, age, null))
            _loginStatus.postValue(true)

            // perform the pw generation
            PasswordGeneratorService.startActionEncrypt(context, username)
        }
    }

    /**
     * TODO THREADING 6: revamp the login task using only coroutines
     * 1. create a suspend function that performs the (mock) pw generation from the IntentService
     * 2. make this fun main-safe by dispatching the task to the appropriate threadpool
     *
     * QNS will this run after app into the background?
     */

    companion object {
        private val TAG = SplashViewModel::class.simpleName
    }
}

/**
 * A factory to create the ViewModel properly.
 * Very boilerplatey code...
 * NOTE This is due to the fact that we have ctor params.
 *   ViewModelProviders manage the lifecycle of VMs and we cannot create VMs by ourselves.
 *   So we need to provide a Factory to ViewModelProviders so that it knows how to create for us
 *   whenever we need an instance of it.
 */
class SplashViewModelFactory(
        private val playerRepo: PlayerRepo,
        private val weatherRepo: WeatherRepo)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(playerRepo, weatherRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
