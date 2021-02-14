package com.boliao.eod

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

/**
 * TODO ARCH 2.2: Manage login data with ViewModel and LiveData (i.e., use MVVM)
 * 1. create this ViewModel class that extends AndroidViewModel so as to be able to retrieve context
 *    NOTE that AndroidViewModel is rather antipattern and having the sharedpref as input is better
 * 2. modify this ViewModel class to take the player repo as input into the ctor
 *    and change the extension to ViewModel() instead of AndroidViewMode(Application)
 */
class SplashViewModel(private val playerRepo: PlayerRepo) // TODO ARCH 3.2:
    : ViewModel() {
//    : AndroidViewModel(application) { // to have context for shared prefs

    // live login status
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    // TODO ARCH 2.3: Manage login data with ViewModel and LiveData (i.e., use MVVM)
    // 1. move the shared prefs setup here
    // 2. create a Mutable and non-mutable LiveData pair to store the login status
//    //    the read-only one for Views to observe
//    private val PREF_FILENAME = "com.boliao.eod.prefs"
//    private val pref: SharedPreferences = getApplication<Application>().applicationContext
//            .getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE)
//    // live login status
//    private val _loginStatus = MutableLiveData<Boolean>()
//    val loginStatus: LiveData<Boolean> = _loginStatus

    // TODO ARCH 3:
    // live member records from the Room DB
    val allPlayers: LiveData<List<Player>> = playerRepo.allPlayers.asLiveData()

    // live weather data (read-only)
    // - this is bound to the mutable one in repo
    var weatherData: LiveData<String>

    init {
        // TODO THREADING 4: replace the stub by the new threaded weather data method
        // - only I control the repo, my boss (Activity) does not need to know about repo
        // WeatherRepo.fetchStaticMockWeatherData()
        // WeatherRepo.fetchDynamicMockWeatherData()

        // TODO NETWORKING 3: call WeatherRepo to fetch online weather instead
        WeatherRepo.fetchOnlineWeatherData()

        // link up live data to repo (observer pattern)
        weatherData = WeatherRepo.weatherData
    }

    /**
     * TODO THREADING
     * Login using a username
     * Runs a coroutine in the VM in-built scope
     * - note that the viewModelScope is an extension func of ViewModel from lifecycle-viewmodel-ktx
     */
//    fun loginWithCoroutines(username:String) = viewModelScope.launch {
//        if (pref.contains(username))
//            _loginStatus.postValue(false)
//        else {
//            // encrypt username
//            val encryptedUsername = encrypt(username)
//
//            // store in pref
//            pref.edit().putString(username, encryptedUsername).apply()
//
//            // update UI
//            _loginStatus.postValue(true)
//        }
//    }

    /**
     * TODO ARCH 2.4: Manage login data with ViewModel and LiveData (i.e., use MVVM)
     * Simply provide a LiveData to track the login username.
     */
//    fun login(username:String) {
//        if (pref.contains(username))
//            _loginStatus.postValue(false)
//        else {
//            // store in pref
//            pref.edit().putString(username, username).apply()
//
//            // update UI
//            _loginStatus.postValue(true)
//        }
//    }

    /**
     * TODO ARCH 3:
     * Use a Room to manage the login data.
     *
     * TODO proper pw and more fields
     */
    fun login(username:String) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val res = playerRepo.getPlayerByName(username)
            Log.d(TAG, "in view model login res=${res.isEmpty()}")

            if(res.isEmpty()) {
                playerRepo.insert(Player(username, ""))
                _loginStatus.postValue(true)
            }
            else {
                _loginStatus.postValue(false)
            }
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
        delay(5000)
        return@withContext username
    }

    override fun onCleared() {
        super.onCleared()

        // remember to play safe, no leaks
        viewModelScope.cancel()
    }

    companion object {
        private val TAG = SplashViewModel::class.simpleName
    }
}

/**
 * A factory to create the ViewModel properly.
 * This is due to the fact that we have ctor params. We cannot create VMs by ourselves so we need
 * to provide a Factory to the ViewModelProviders so that it knows how to create for us whenever we
 * need an instance of it.
 * Very boilerplatey code...
 */
class SplashViewModelFactory(private val playerRepo: PlayerRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(playerRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}