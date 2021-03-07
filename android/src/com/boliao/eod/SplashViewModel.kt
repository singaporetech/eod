package com.boliao.eod

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

/**
 * TODO ARCH 2.2: Manage login data with ViewModel and LiveData (i.e., use MVVM)
 * 1. create this ViewModel class that extends AndroidViewModel so as to be able to retrieve context
 *
 * TODO ARCH 3.6: Manage membership data with a Room
 * 1. modify this ViewModel class to take the player repo as input into the ctor
 * 2. change the extension to ViewModel() instead of AndroidViewMode(Application)
 * 3. create a factory that extends from ViewModelProvider.Factory
 *    NOTE This is due to the fact that we have ctor params.
 *    ViewModelProviders manage the lifecycle of VMs and we cannot create VMs by ourselves.
 *    So we need to provide a Factory to ViewModelProviders so that it knows how to create for us
 *    whenever we need an instance of it.
 */
class SplashViewModel(
        private val playerRepo: PlayerRepo,
        private val weatherRepo: WeatherRepo) // TODO ARCH 3.2:
    : ViewModel() {
//    : AndroidViewModel(application) { // to have context for shared prefs

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

    // TODO ARCH 3.6: Manage membership data with a Room
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

        // TODO NETWORKING 2: call WeatherRepo to fetch online weather instead
        viewModelScope.launch {
            weatherRepo.fetchOnlineWeatherData()
        }

        // link up live data to repo (observer pattern)
        weatherData = weatherRepo.weatherData
    }


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
     * TODO ARCH 3.6: Manage membership data with a Room
     * Use a Room to manage the login data.
     */
//    fun login(username:String, age:Int?/*, pw:String?*/) = viewModelScope.launch(Dispatchers.IO) {
//        Log.d(TAG, "in view model login ${playerRepo.contains(username)}")
//
//        // TODO SERVICES 3.3: encrypt username before storing
//        // NOTE that we don't really need to wait for the pw to be generated before we allow login
//        // NOTE that although no more ANR, it still disrupts the UX as incuring unnecessary wait
//        val pw = getEncryptedPw(username)
//
//        if(playerRepo.contains(username)) {
//            _loginStatus.postValue(false)
//        }
//        else {
//            // do the db IO in a dedicated "thread"
//            playerRepo.insert(Player(username, age, pw))
//            _loginStatus.postValue(true)
//        }
//    }

    /**
     * TODO SERVICES 3.2: Shift the pseudo-encryption function here.
     *
     * A function that simply sleeps for 5s to mock a cpu-intensive task
     * It also does some amazing manip to the name string
     * @param name of the record to generate pw for
     * @return (pseudo-)encrypted pw String
     */
//    private fun getEncryptedPw(name: String): String {
//        Thread.sleep(5000)
//        return name + "888888"
//    }

    /**
     * TODO SERVICES 2.3: login with pw generation using an intent service
     * 1. create the IntentService class
     * 2. defer the heavy lifting pw generation task
     * 3. let it store the encrypted pw into the db when it is done
     * @return (pseudo-)encrypted String
     */
//    fun login(context: Context, username:String, age:Int?) = viewModelScope.launch(Dispatchers.IO) {
//        Log.d(TAG, "in view model login ${playerRepo.contains(username)}")
//
//        if(playerRepo.contains(username)) {
//            _loginStatus.postValue(false)
//        }
//        else {
//            playerRepo.insert(Player(username, age, null))
//            _loginStatus.postValue(true)
//
//            // perform the pw generation
//            PasswordGeneratorService.startActionEncrypt(context, username)
//        }
//    }


    /**
     * TODO THREADING 3*: coroutine approach for login task.
     * Login using a username
     * Runs a coroutine in the VM in-built scope
     * - note that the viewModelScope is an extension func of ViewModel from lifecycle-viewmodel-ktx
     */
    fun loginWithCoroutines(username:String, age:Int?) = viewModelScope.launch(Dispatchers.IO) {
        if (playerRepo.contains(username))
            _loginStatus.postValue(false)
        else {
            playerRepo.insert(Player(username, age, null))
            _loginStatus.postValue(true)

            // perform the pw generation
            generatePwAndUpdate(username)
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
     *
     * NOTE that we use withContext to make this function independently main-safe so that it does
     *      not matter what coroutine dispatcher context the caller is in
     * NOTE that this will likely still continue to finish even if the app is placed in the
     *      background. This makes intent services obsolete.
     */
    private suspend fun generatePwAndUpdate(username: String) = withContext(Dispatchers.IO) {
        Thread.sleep(5000)
        val pw = username + "888888"
        playerRepo.updatePw(username, pw)

        // DEBUG fetch and log
        delay(6000) // coroutine method
        val players = playerRepo.getPlayer(username)
        Log.d(TAG, "in generatePwAndUpdate just added pw = ${players[0].pw}")
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
 * TODO ARCH 3.6: Manage membership data with a Room
 * A factory to create the ViewModel properly.
 * Very boilerplatey code...
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
