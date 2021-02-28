package com.boliao.eod

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

/**
 * Splash VM to manage the data logic between splash view and the model
 *
 */
class SplashViewModel(private val playerRepo: PlayerRepo): ViewModel() {

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

    /**
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
 * Very boilerplatey code...
 * NOTE This is due to the fact that we have ctor params.
 *   ViewModelProviders manage the lifecycle of VMs and we cannot create VMs by ourselves.
 *   So we need to provide a Factory to ViewModelProviders so that it knows how to create for us
 *   whenever we need an instance of it.
 */
class SplashViewModelFactory(private val playerRepo: PlayerRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(playerRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}